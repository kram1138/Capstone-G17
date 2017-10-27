/*
 * Demo line-following code for the Pololu Zumo Robot
 *
 * This code will follow a black line on a white background, using a
 * PID-based algorithm.  It works decently on courses with smooth, 6"
 * radius curves and has been tested with Zumos using 30:1 HP and
 * 75:1 HP motors.  Modifications might be required for it to work
 * well on different courses or with different motors.
 *
 * http://www.pololu.com/catalog/product/2506
 * http://www.pololu.com
 * http://forum.pololu.com
 */

#include <QTRSensors.h>
#include <ZumoReflectanceSensorArray.h>
#include <ZumoMotors.h>
#include <ZumoBuzzer.h>
#include <Pushbutton.h>


ZumoBuzzer buzzer;
ZumoReflectanceSensorArray reflectanceSensors;
ZumoMotors motors;
Pushbutton button(ZUMO_BUTTON);
int lastError = 0;
const int numOfSensors = 6;
const int minLineFound = 750;

// This is the maximum speed the motors will be allowed to turn.
// (400 lets the motors go at top speed; decrease to impose a speed limit)
const int MAX_SPEED = 150;


void setup()
{
  Serial.begin(9600);
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
boolean centered(unsigned int sensors[numOfSensors]){
//  unsigned int time = 0;
//  time = micros();
  
  boolean isCentered;
  boolean sensorsOnLine[numOfSensors];
  
  sensorsOnLine[0] = sensors[0] >= minLineFound ? true : false;
  sensorsOnLine[1] = sensors[1] >= minLineFound ? true : false;
  sensorsOnLine[2] = sensors[2] >= minLineFound ? true : false;
  sensorsOnLine[3] = sensors[3] >= minLineFound ? true : false;
  sensorsOnLine[4] = sensors[4] >= minLineFound ? true : false;
  sensorsOnLine[5] = sensors[5] >= minLineFound ? true : false;
  
  if (sensorsOnLine[0] == false and sensorsOnLine[1] == false and (sensorsOnLine[2] == true or
      sensorsOnLine[3] == true) and sensorsOnLine[4] == false and sensorsOnLine[5] == false) {
    isCentered = true;
  } else {
    isCentered = false;
  }

//  Serial.print(" "); Serial.print(isCentered); Serial.print(" | ");
//  for(int i=0; i< 6; i++) {
//    Serial.print(sensors[i]);
//    Serial.print(",");
//  }
//  Serial.println();

//  time = micros() - time;
//  Serial.print("Centered Function Time: ");
//  Serial.println(time);
  return isCentered;
}

void loop()
{
  unsigned int sensors[numOfSensors];

  // Get the position of the line.  Note that we *must* provide the "sensors"
  // argument to readLine() here, even though we are not interested in the
  // individual sensor readings
  int position = reflectanceSensors.readLine(sensors);
//  Serial.print(position);

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
  
    lastError = error;
  
    // Get individual motor speeds.  The sign of speedDifference
    // determines if the robot turns left or right.
    int m1Speed = MAX_SPEED + speedDifference;
    int m2Speed = MAX_SPEED - speedDifference;
  
    // Here we constrain our motor speeds to be between 0 and MAX_SPEED.
    // Generally speaking, one motor will always be turning at MAX_SPEED
    // and the other will be at MAX_SPEED-|speedDifference| if that is positive,
    // else it will be stationary.  For some applications, you might want to
    // allow the motor speed to go negative so that it can spin in reverse.
    if (m1Speed < 0)
      m1Speed = 0;
    if (m2Speed < 0)
      m2Speed = 0;
    if (m1Speed > MAX_SPEED)
      m1Speed = MAX_SPEED;
    if (m2Speed > MAX_SPEED)
      m2Speed = MAX_SPEED;

//    time = micros() - time;
//    Serial.print("Position Adjustment Time: ");
//    Serial.println(time);
    motors.setSpeeds(m1Speed, m2Speed);
  } else {
    motors.setSpeeds(MAX_SPEED,MAX_SPEED);
  }
}
