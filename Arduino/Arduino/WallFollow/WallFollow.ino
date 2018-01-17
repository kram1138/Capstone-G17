#include <ZumoMotors.h>
#include <math.h>
#include <QTRSensors.h>
#include <ZumoReflectanceSensorArray.h>
#include <ZumoMotors.h>
#include <Pushbutton.h>
#include <SoftwareSerial.h>
#include <String.h>
//#include <Regexp.h>

#define LED_PIN 13

SoftwareSerial mySerial(1, 0); // set bluetooth serial communication

ZumoReflectanceSensorArray reflectanceSensors;
ZumoMotors motors;
Pushbutton button(ZUMO_BUTTON);// set zumo button

const char CONSEC_A = 'A'; // char A to be used in consecutive check
const char CONSEC_O = 'O'; // char O to be used in consecutive check
const char ETB = 0x17;// END OF TRANSMISSION BLOCK 0x0D;//
const int CONSEC_MIN_LEN = 5; // min length for consecutive check
const int MAX_SPEED = 150;// max allowed speed for the motors to turn. Top speed is 400.
const int MIN_CONSEC_COUNT = 3;// min consecutive times line read to eliminate false positive.
const int MIN_LINE_FOUND = 750;// min value from refletance sensor to confirm line is found
const int NUM_OF_SENSORS = 6;// number of reflectance sensors
const int PRINT_STR_BUFFER = 128;// print string buffer size
const int REFL_SENSOR_LEFT_END = 0;// sensors array position of the left most reflectance sensor
const int REFL_SENSOR_LEFT_MIDDLE = 2;// sensors array position of the left center reflectance sensor
const int REFL_SENSOR_RIGHT_END = 5;// sensors array possition of the right most reflectance sensor
const int REFL_SENSOR_RIGHT_MIDDLE = 3;// sensors array possition of the right center reflectance sensor
const int RESET = 0;// used to reset int value to zero
const String LEFT_ROOM = "lr";
const String RIGHT_ROOM = "rr";
const String LEFT_INTERSECTION = "li";
const String RIGHT_INTERSECTION = "ri";
const char *DELIMITER_SPACE = {' '};
const char *DELIMITER_UNDERSCORE = {'_'};

struct turn
{
  String turnDirection;
  int turnNode;
};

typedef struct turn Turn;

// declare variables
boolean incCmd = false;// variable keeps track if full command is read or not
char printStr[PRINT_STR_BUFFER];// char array buffer for writing msgs to serial
int mLSpeed = 0;// left side motors speed
int mRSpeed = 0;// right side motors speed
int lastError = 0;// holds the last error value for PID calculation
int max_speed = MAX_SPEED;// max allowed speed
String bData = "";// string to store recieved serial commands
String mapData = "";
unsigned int sensors[NUM_OF_SENSORS];// array to store values from reflectance sensors
Turn *turnsArr;

// declare node id variables
int countL = 0;// counting left node's consecutive readings
int countR = 0;// counting right node's consecutive readings
int currTurn = 0;
int nodeL = 0;// counting number of left nodes
int nodeR = 0;// counting number of right nodes

const int delayTime = 500;
const int threshold = 20;
int state;
int cleanState;
int dir;
int lastTime;
int numTurns;

void setup()
{
  button.waitForButton();
  mySerial.begin(115200);// set bluetooth serial baud rate of 115200
  mySerial.println("Bluetooth Communication Established!");

  // load map via bluetooth
  loadMap();
  mySerial.println("Out of LoadMap!");
  mySerial.println();

  // Initialize the reflectance sensors module
  reflectanceSensors.init();

  // Wait for the user button to be pressed and released
  button.waitForButton();

  // Turn on LED to indicate we are in calibration mode
  pinMode(13, OUTPUT);
  digitalWrite(13, HIGH);

  // Wait 1 second and then begin automatic sensor calibration
  // by rotating in place to sweep the sensors over the line
  delay(1000);
  int i;
  for (i = 0; i < 80; i++)
  {
    if ((i > 10 && i <= 30) || (i > 50 && i <= 70))
      motors.setSpeeds(-200, 200);
    else
      motors.setSpeeds(200, -200);
    reflectanceSensors.calibrate();

    delay(20);
  }
  motors.setSpeeds(0, 0);

  // Turn off LED to indicate we are through with calibration
  digitalWrite(13, LOW);

  button.waitForButton();

  state = 0;
  cleanState = 0;
  int dir;
  lastTime = 0;
}

void loop()
{
  float s1 = pow(analogRead(A0) / 5970.8, 1 / -.886);
  float s2 = pow(analogRead(A1) / 4205.5, 1 / -.777);
  float s3 = pow(analogRead(A2) / 2401.6, 1 / -.575);

  if (state == 0)
  {
    Navigate();
  }
  else if (state == 1)
  {
    Clean(s1, s2, s3, threshold);
    if (millis() - lastTime > 10 * 1000)
      state = 2;
  }
  else if (state == 2)
  {
    FollowWall(s1, s2, s3);
  }

  delay(10);
}

void Navigate()
{
  String bData;
  unsigned long lastTime = 0;

  // Get the position of the line.  Note that we *must* provide the "sensors"
  // argument to readLine() here, even though we are not interested in the
  // individual sensor readings
  int position = reflectanceSensors.readLine(sensors);

  // Get string from bluetooth serial every delayTime in ms and compare it to
  // "pos", "speed", "start" and "stop" commands. Then execute that specific
  // part of code otherwise write invalid command to bluetooth serial.
  if ((millis() - lastTime) > delayTime) {
    // read incoming string
    bData = readCmdFromBluetooth(true);

    if (bData.length() > 0) {
      if (bData == "pos") { //
        snprintf(printStr, PRINT_STR_BUFFER, "position: %d", position);
        mySerial.print(printStr);
        mySerial.print(ETB);
      } else if (bData == "speed") {
        snprintf(printStr, PRINT_STR_BUFFER, "mLSpeed: %d; mRSpeed: %d", mLSpeed, mRSpeed);
        mySerial.print(printStr);
        mySerial.print(ETB);
      } else if (bData == "stop") {
        mySerial.print("Stopping...");
        mySerial.print(ETB);
        max_speed = 0;
        motors.setSpeeds(max_speed, max_speed);
      } else if (bData == "start") {
        mySerial.print("Starting...");
        mySerial.print(ETB);
        max_speed = MAX_SPEED;
        motors.setSpeeds(max_speed, max_speed);
      } else {
        snprintf(printStr, PRINT_STR_BUFFER, "Invalid Command: %s", bData.c_str());
        mySerial.print(printStr);
        mySerial.print(ETB);
      }
    }
    lastTime = millis();
  }

  // call method to check if arrived at a node
  checkNodes();

  // ONLY correct direction if the line is not detected from one of the middle
  // sensors or if the two sensors on both end detect a line.
  if (!centered()) {

    // Our "error" is how far we are away from the center of the line, which
    // corresponds to position 2500.
    int error = position - 2500;

    // Get motor speed difference using proportional and derivative PID terms
    // (the integral term is generally not very useful for line following).
    // Here we are using a proportional constant of 1/4 and a derivative
    // constant of 6, which should work decently for many Zumo motor choices.
    // You probably want to use trial and error to tune these constants for
    // your particular Zumo and line course.
    int speedDifference = error / 4 + 6 * (error - lastError);
    //    mySerial.println(speedDifference);
    lastError = error;

    // Get individual motor speeds.  The sign of speedDifference
    // determines if the robot turns left or right.
    mLSpeed = max_speed + speedDifference;
    mRSpeed = max_speed - speedDifference;

    // Here we constrain our motor speeds to be between 0 and max_speed.
    // Generally speaking, one motor will always be turning at max_speed
    // and the other will be at max_speed-|speedDifference| if that is positive,
    // else it will be stationary.  For some applications, you might want to
    // allow the motor speed to go negative so that it can spin in reverse.
    if (mLSpeed < 0)
      mLSpeed = 0;
    if (mRSpeed < 0)
      mRSpeed = 0;
    if (mLSpeed > max_speed)
      mLSpeed = max_speed;
    if (mRSpeed > max_speed)
      mRSpeed = max_speed;

    motors.setSpeeds(mLSpeed, mRSpeed);
  } else {
    motors.setSpeeds(max_speed, max_speed);
  }
}

void FollowWall(float s1, float s2, float s3)
{
  float theta = atan(s2 / s1);
  float d = s1 * sin(theta);

  if (s1 < 100 && s2 < 100 && s3 > 15)
  {
    float angle = (theta * 180 / PI) - 45;
    if (d < 15)
    {
      if ((angle > -15 && d < 10) || angle > 15)
      {
        motors.setLeftSpeed(100);
        motors.setRightSpeed(200);
      }
      else
      {
        motors.setLeftSpeed(200);
        if (100 + (20 + angle) * 10 < 200 && 100 + (20 + angle) * 10 > 0)
          motors.setRightSpeed(100 + (20 + angle) * 10);
        else if (100 + (20 + angle) * 10 >= 200)
          motors.setRightSpeed(200);
        else
          motors.setRightSpeed(0);
      }
    }
    else
    {
      if ((angle < 15 && d > 20) || angle < -15)
      {
        motors.setLeftSpeed(200);
        motors.setRightSpeed(100);
      }
      else
      {
        if (100 + (20 - angle) * 10 < 200 && 100 + (20 - angle) * 10 > 0)
          motors.setLeftSpeed(100 + (20 - angle) * 10);
        else if (100 + (20 - angle) * 10 >= 200)
          motors.setLeftSpeed(200);
        else
          motors.setLeftSpeed(0);

        motors.setRightSpeed(200);
      }
    }
  }
  else if (s1 < 100 && s2 < 100 && s3 <= 15)
  {
    motors.setLeftSpeed(-150);
    motors.setRightSpeed(150);
    delay(350);
    motors.setLeftSpeed(0);
    motors.setRightSpeed(0);
  }
  else if (s1 >= 100 && s2 < 100)
  {
    motors.setLeftSpeed(200);
    motors.setRightSpeed(100);
  }
  else
  {
    motors.setLeftSpeed(150);
    motors.setRightSpeed(150);
  }
}

void Clean(float s1, float s2, float s3, float threshold)
{
  if (cleanState == 0)
  {
    if (s3 < threshold || s1 < threshold)
    {
      cleanState = 1;
      if (s1 < 100 || s2 < 100)
        dir = 0;
      else
        dir = 1;
    }
    else
    {
      motors.setLeftSpeed(150);
      motors.setRightSpeed(150);
    }
  }
  else if (cleanState == 1)
  {
    if (random(5) == 0 && s1 > 100 && s3 > 100)
    {
      cleanState = 0;
    }
    else
    {
      if (dir == 0)
      {
        motors.setLeftSpeed(-150);
        motors.setRightSpeed(150);
      }
      else
      {
        motors.setLeftSpeed(150);
        motors.setRightSpeed(-150);
      }
    }
  }
}

void splitByDelimiterIntoArray(String str, String *strArr, char delimiter, const int strArrSize) {
  int index = 0;
  int prevIndex = 0;
  int strIndex = 0;

  do {
    index = str.indexOf(delimiter);

    if (index != -1) {
      strArr[strIndex] = str.substring(prevIndex, index);
      str = str.substring(index + 1);
    } else {
      strArr[strIndex] = str.substring(0);
      str = "";
    }

    strIndex++;
  } while (str.length() > 0 and strIndex < strArrSize);
}

void populateTurnsArr(String *arr, const int arrSize) {
  int arrIndex = 2;

  for (int index = 0; index < arrSize; index++) {
    String tempArr[arrIndex];
    splitByDelimiterIntoArray(arr[index], tempArr, DELIMITER_UNDERSCORE, arrIndex);

    snprintf(printStr, PRINT_STR_BUFFER, "\nTurn: %s; %s\n", tempArr[0].c_str(), tempArr[1].c_str());
    mySerial.println(printStr);

    turnsArr[index].turnDirection = tempArr[0];
    turnsArr[index].turnNode = tempArr[1].toInt();
  }
}

bool checkForConsecutives(String msg, char consec)
{
  bool found = false;
  int consecCount = 0;

  for (int i = 0; i < msg.length(); i++)
  {
    if (msg[i] == consec)
    {
      consecCount++;
    } else {
      consecCount = 0;
    }
  }
  
  if (consecCount >= CONSEC_MIN_LEN)
  {
    found = true;
  }
  
  return found;
}

void getMapDataString()
{
  do
  {
    mapData = readCmdFromBluetooth(false);
  } while (!(mapData.length() > 0));

//  mySerial.println("MapData length > 0!");
//  mySerial.println();
//
//  mySerial.println("Read Data:");
  mySerial.println(mapData);
  mySerial.println();
}

bool getMapDataResponse()
{
  bool result = false;
  bool resultA = false;
  bool resultO = false;
  String response = "";

  do
  {
    response = readCmdFromBluetooth(false);

  } while (!(response.length() > 0));

  resultA = checkForConsecutives(response,CONSEC_A);
  resultO = checkForConsecutives(response,CONSEC_O);

  if (!resultO && resultA)
  {
    result = true;
  } else if (resultO && !resultA)
  {
    result = false;
  } else
  {
    mySerial.println("Not found either consecutive A's or R's!");
    mySerial.println();
  }
  
  return result;
}

String getMapData()
{
  bool correctMapData = false;

  do
  {
    getMapDataString();
    correctMapData = getMapDataResponse();

  } while (!correctMapData);

  return mapData;
}

void loadMap()
{
  String mapData = "";

  mySerial.println("Starting loop to receive data...");
  mySerial.println();
  
  mapData = getMapData();

  int spaceIndex = mapData.indexOf(' ');
  numTurns = mapData.substring(0, spaceIndex).toInt();
  turnsArr = malloc(sizeof(Turn) * numTurns);
  //      char *tempArr[numTurns];
  String dataArr[numTurns];
  //      char mapDataCharArr[(mapData.substring(mapData.indexOf(' '))).length()+1];
  String data = mapData.substring(spaceIndex + 1);

  // ignore count in the mapData for tokenizing
  //      mapData.substring(mapData.indexOf(' ')).toCharArray(mapDataCharArr,(mapData.substring(mapData.indexOf(' '))).length()+1);

  snprintf(printStr, PRINT_STR_BUFFER, "\nData: %s\n", data.c_str());
  mySerial.println(printStr);

  // split the mapDataCharArr by space
  splitByDelimiterIntoArray(data, dataArr, DELIMITER_SPACE, numTurns);
  //      splitByDelimiterIntoArray(mapDataCharArr,tempArr,numTurns,DELIMITER_SPACE);

  snprintf(printStr, PRINT_STR_BUFFER, "\nSpace: %s\n", dataArr[0].c_str());
  mySerial.println(printStr);
  mySerial.println("Done split by space!");
  mySerial.println();

  // use tempArr to create Turn objects and add them to turnsArr
  populateTurnsArr(dataArr, numTurns);
  mySerial.println("Done populating TurnsArr!");
  mySerial.println();

  for (int i = 0; i < numTurns; i++) {
    snprintf(printStr, PRINT_STR_BUFFER, "Turn: %s; Node: %d", (turnsArr[i].turnDirection).c_str(), turnsArr[i].turnNode);
    mySerial.println(printStr);
    mySerial.println();
  }
}

// This method takes in the value from the 6 sensors, then creates a
// boolean array with value true if that sensor found the line otherwise
// inputs value false. It then sets the isCentered boolean variable to
// true only if either the two middle sensors detected and the number of
// lines detected is less
boolean centered() {
  //  unsigned int time = 0;
  //  time = micros();

  boolean isCentered;

  // Sets isCentered boolean to true if only either the middle two sensors
  // find the line otherwise boolean value of false is set.
  if ((sensors[0] < MIN_LINE_FOUND and sensors[5] < MIN_LINE_FOUND and
       sensors[1] < MIN_LINE_FOUND and sensors[4] < MIN_LINE_FOUND) and
      (sensors[REFL_SENSOR_LEFT_MIDDLE] > MIN_LINE_FOUND or
       sensors[REFL_SENSOR_RIGHT_MIDDLE] > MIN_LINE_FOUND))
  {
    isCentered = true;
  } else {
    isCentered = false;
  }

  //  mySerial.print(" "); mySerial.print(isCentered); mySerial.print(" | ");
  //  for(int i=0; i< 6; i++) {
  //    mySerial.print(sensors[i]);
  //    mySerial.print(",");
  //  }
  //  mySerial.println();

  //  time = micros() - time;
  //  snprintf(printStr,PRINT_STR_BUFFER,"Centered Function Time: %d",time);
  //  mySerial.print(printStr);
  return isCentered;
}

// This method reads each character from the bluetooth serial every 300ms
// while there are bytes available in input buffer or "enter" (0x0D) is
// read. It stores the characters read in String called bData which gets
// returned after it is trimmed and printed to bluetooth serial.
String readCmdFromBluetooth(bool printBData) {
  //  String bData = "";
  char temp;

  if (!incCmd) {
    bData = "";
    incCmd = true;
  }

  while (mySerial.available() > 0) {
    temp = mySerial.read();//gets one byte from serial buffer
    if (temp == ETB) {// full command received after ETB (0x17) is read
      incCmd = false;
      break;
    }//breaks out of capture loop when enter is pressed

    bData += temp;

    delay(10);//small delay to allow input buffer to fill
  }//makes the string bData

  if (bData != "" and !incCmd) {
    bData.trim();

    if (printBData)
    {
      snprintf(printStr, PRINT_STR_BUFFER, "\nbData: %s", bData.c_str());
      mySerial.println(printStr);
    }    
    //    mySerial.println("Current bData:");
    //    mySerial.println(bData);
    //    mySerial.println();
    return bData;
  } else {
    //    mySerial.println("Current bData:");
    //    mySerial.println(bData);
    //    mySerial.println();
    return (String)"";
  }
}

void checkNodes()
{
  // check line read at left end sensor
  if (sensors[REFL_SENSOR_LEFT_END] > MIN_LINE_FOUND)
  {
    countL++;

    // increment nodeL if read line at left sensor MIN_CONSEC_COUNT times
    if (countL == MIN_CONSEC_COUNT)
    {
      nodeL++;
      checkToTurn();
    }
  } else // reset countL if no line was found
  {
    countL = RESET;
  }

  // check line read at right end sensor
  if (sensors[REFL_SENSOR_RIGHT_END] > MIN_LINE_FOUND)
  {
    countR++;

    // increment nodeR if read line at right sensor MIN_CONSEC_COUNT times
    if (countR == MIN_CONSEC_COUNT)
    {
      nodeR++;
      checkToTurn();
    }
  } else // reset countR if no line was found
  {
    countR = RESET;
  }
}

void waitWhileTurning()
{
  bool fullyTurned = false;

  while (!fullyTurned) {
    reflectanceSensors.readLine(sensors); // read data from reflectance sensors

    //    snprintf(printStr,PRINT_STR_BUFFER,"Sensors: %d, %d, %d, %d, %d, %d",sensors[0],sensors[1],sensors[2],sensors[3],sensors[4],sensors[5]);
    //    mySerial.println(printStr);

    if (centered())
    {
      fullyTurned = true;
    }
  }

  mySerial.println("Turning completed!"); mySerial.println();

  // reset motor speeds to maxSpeed
  motors.setSpeeds(max_speed, max_speed);
}

void checkToTurn()
{
  snprintf(printStr, PRINT_STR_BUFFER, "Nodes on Left: %d\nNodes on Right: %d\n", nodeL, nodeR);
  mySerial.println(printStr);
  mySerial.println(numTurns);
  // proceeds if there are more turns to make
  if (currTurn < numTurns) {
    Turn theTurn = turnsArr[currTurn];// get curr turn

    if (theTurn.turnDirection == LEFT_INTERSECTION)
    {
      // if curr turn direction is left
      // and turn node equals nodeL then
      // it properly turns
      if (theTurn.turnNode == nodeL)
      {
        mySerial.println("Turning left...");
        currTurn++;
        motors.setSpeeds(-100, 150);
        waitWhileTurning();
      }
    } else { // right direction
      // if curr turn direction is right
      // and turn node equals nodeR then
      // it properly turns
      if (theTurn.turnNode == nodeR)
      {
        mySerial.println("Turning right...");
        currTurn++;
        motors.setSpeeds(150, -100);
        waitWhileTurning();
      }
    }
  } else {
    mySerial.println("Finished turning instructions!");
  }
}

