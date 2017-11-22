#include <QTRSensors.h>
#include <ZumoReflectanceSensorArray.h>
#include <ZumoMotors.h>
//#include <ZumoBuzzer.h>
#include <Pushbutton.h>
#include <SoftwareSerial.h>


SoftwareSerial mySerial(0,1);// set bluetooth serial communication
//ZumoBuzzer buzzer;
ZumoReflectanceSensorArray reflectanceSensors;
ZumoMotors motors;
Pushbutton button(ZUMO_BUTTON);


const int MAX_SPEED = 150;// max allowed speed for the motors to turn. Top speed is 400.
const int MIN_LINE_FOUND = 750;// min value from refletance sensor to confirm line is found
const int NUM_OF_SENSORS = 6;// number of reflectance sensors
const int PRINT_STR_BUFFER = 128;// print string buffer size

boolean incCmd = false;// variable keeps track if full command is read or not
char printStr[PRINT_STR_BUFFER];
int m1Speed = 0;
int m2Speed = 0;
int lastError = 0;
int max_speed = MAX_SPEED;
String bData = "";

void setup()
{
  mySerial.begin(9600);// set bluetooth serial baud rate of 9600
  mySerial.println("Bluetooth Communication Established!");
  // Play a little welcome song
//  buzzer.play(">g32>>c32");

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
  for(i = 0; i < 80; i++)
  {
    if ((i > 10 && i <= 30) || (i > 50 && i <= 70))
      motors.setSpeeds(-200, 200);
    else
      motors.setSpeeds(200, -200);
    reflectanceSensors.calibrate();

    // Since our counter runs to 80, the total delay will be
    // 80*20 = 1600 ms.
    delay(20);
  }
  motors.setSpeeds(0,0);

  // Turn off LED to indicate we are through with calibration
  digitalWrite(13, LOW);
//  buzzer.play(">g32>>c32");

  // Wait for the user button to be pressed and released
  button.waitForButton();

  // Play music and wait for it to finish before we start driving.
//  buzzer.play("L16 cdegreg4");
//  while(buzzer.isPlaying());
}

// This method takes in the value from the 6 sensors, then creates a
// boolean array with value true if that sensor found the line otherwise
// inputs value false. It then sets the isCentered boolean variable to 
// true only if either the two middle sensors detected and the number of
// lines detected is less
boolean centered(unsigned int sensors[NUM_OF_SENSORS]){
//  unsigned int time = 0;
//  time = micros();
  
  boolean isCentered;
  boolean sensorsOnLine[NUM_OF_SENSORS];

  // fill boolean reflectance sensor array according to if line is found
  sensorsOnLine[0] = sensors[0] >= MIN_LINE_FOUND ? true : false;
  sensorsOnLine[1] = sensors[1] >= MIN_LINE_FOUND ? true : false;
  sensorsOnLine[2] = sensors[2] >= MIN_LINE_FOUND ? true : false;
  sensorsOnLine[3] = sensors[3] >= MIN_LINE_FOUND ? true : false;
  sensorsOnLine[4] = sensors[4] >= MIN_LINE_FOUND ? true : false;
  sensorsOnLine[5] = sensors[5] >= MIN_LINE_FOUND ? true : false;

  // if only either the middle two sensors detect the line then isCentered is set to true,
  // othwerwise it will be set to false
  if (sensorsOnLine[0] == false and sensorsOnLine[1] == false and (sensorsOnLine[2] == true or
      sensorsOnLine[3] == true) and sensorsOnLine[4] == false and sensorsOnLine[5] == false) {
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
String readCmdFromBluetooth() {
//  String bData = "";
  char temp;

  if (!incCmd) {
    bData = "";
    incCmd = true;
  }
  
  while (mySerial.available() > 0) {
    temp = mySerial.read();//gets one byte from serial buffer
    if (temp == 0x0D) {// full command received after "enter" (0x0D) is read
      incCmd = false;
      break;
    }//breaks out of capture loop when enter is pressed
    
    bData += temp;
    
    delay(10);//small delay to allow input buffer to fill
  }//makes the string bData  

  if (bData != "" and !incCmd) {
    bData.trim();
    snprintf(printStr,PRINT_STR_BUFFER,"\nbData: %s",bData.c_str());
    mySerial.println(printStr);
    return bData;
  } else {
    return (String)"";
  }  
}

void loop()
{
  const int delayTime = 500;
  String bData;
  unsigned long lastTime = 0;
  unsigned int sensors[NUM_OF_SENSORS];

  // Get the position of the line.  Note that we *must* provide the "sensors"
  // argument to readLine() here, even though we are not interested in the
  // individual sensor readings
  int position = reflectanceSensors.readLine(sensors);

  // Get string from bluetooth serial every delayTime in ms and compare it to
  // "pos", "speed", "start" and "stop" commands. Then execute that specific
  // part of code otherwise write invalid command to bluetooth serial.
  if ((millis() - lastTime) > delayTime) {
    // read incoming string
    bData = readCmdFromBluetooth();

    if (bData.length() > 0) {
      if (bData == "pos"){// 
        snprintf(printStr,PRINT_STR_BUFFER,"position: %d",position);
        mySerial.println(printStr);
      } else if (bData == "speed"){
        snprintf(printStr,PRINT_STR_BUFFER,"m1Speed: %d; m2Speed: %d",m1Speed,m2Speed);
        mySerial.println(printStr);
      } else if (bData == "stop") {
        mySerial.println("Stopping...");
        max_speed = 0;
        motors.setSpeeds(max_speed, max_speed);
      } else if (bData == "start") {
        mySerial.println("Starting...");
        max_speed = MAX_SPEED;
        motors.setSpeeds(max_speed, max_speed);
      } else {
        snprintf(printStr,PRINT_STR_BUFFER,"Invalid Command: %s",bData.c_str());
        mySerial.println(printStr);
      }
    }
    lastTime = millis();
  }

  // ONLY correct direction if the line is not detected from one of the middle
  // sensors or if the two sensors on both end detect a line.
  if (!centered(sensors)) {
//    unsigned int time = 0;
//    time = micros();
    
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
    m1Speed = max_speed + speedDifference;
    m2Speed = max_speed - speedDifference;
  
    // Here we constrain our motor speeds to be between 0 and max_speed.
    // Generally speaking, one motor will always be turning at max_speed
    // and the other will be at max_speed-|speedDifference| if that is positive,
    // else it will be stationary.  For some applications, you might want to
    // allow the motor speed to go negative so that it can spin in reverse.
    if (m1Speed < 0)
      m1Speed = 0;
    if (m2Speed < 0)
      m2Speed = 0;
    if (m1Speed > max_speed)
      m1Speed = max_speed;
    if (m2Speed > max_speed)
      m2Speed = max_speed;

//    time = micros() - time;
//    snprintf(printStr,PRINT_STR_BUFFER,"Position Adjustment Time: %d",time);
//    mySerial.print(printStr);
    motors.setSpeeds(m1Speed, m2Speed);
  } else {
    motors.setSpeeds(max_speed,max_speed);
  }
}
