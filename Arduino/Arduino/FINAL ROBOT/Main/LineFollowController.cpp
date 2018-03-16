#include "Robot.h"
#include "Bluetooth.h"
#include "LineFollowController.h"

LineFollowController::LineFollowController(Robot * newRobot)
{
  robot = newRobot;
}

void LineFollowController::Init(char * newMap)
{
  lastError = 0;
  countR = 0;
  countL = 0;
  currTurn = 0;
  mapArray = newMap;
 Serial.println(strlen(mapArray));
  for (int i; i < strlen(mapArray); i++)
  {
    Serial.println(mapArray[i]);
  }
}

void LineFollowController::Navigate()
{
  String bData;
  unsigned long lastTime = 0;

  // call method to check if arrived at a node
  CheckNodes();

  // ONLY correct direction if the line is not detected from one of the middle
  // sensors or if the two sensors on both end detect a line.
  if (!Centered())
  {
    // Get the position of the line.  Note that we *must* provide the "sensors"
    // argument to readLine() here, even though we are not interested in the
    // individual sensor readings
    int position = robot->ReflectanceSensorPos();
    
    // Our "error" is how far we are away from the center of the line, which
    // corresponds to position 2500.
    int error = position - 2500;
    //Serial.println(error);
    // Get motor speed difference using proportional and derivative PID terms
    // (the integral term is generally not very useful for line following).
    // Here we are using a proportional constant of 1/4 and a derivative
    // constant of 6, which should work decently for many Zumo motor choices.
    // You probably want to use trial and error to tune these constants for
    // your particular Zumo and line course.
    int speedDifference = error / 4 + 6 * (error - lastError);
    //    Serial.println(speedDifference);
    lastError = error;

    // Get individual motor speeds.  The sign of speedDifference
    // determines if the robot turns left or right.
    int mLSpeed = MAX_SPEED + speedDifference;
    int mRSpeed = MAX_SPEED - speedDifference;

    // Here we constrain our motor speeds to be between 0 and MAX_SPEED.
    // Generally speaking, one motor will always be turning at MAX_SPEED
    // and the other will be at MAX_SPEED-|speedDifference| if that is positive,
    // else it will be stationary.  For some applications, you might want to
    // allow the motor speed to go negative so that it can spin in reverse.
    if (mLSpeed < 0)
      mLSpeed = 0;
    if (mRSpeed < 0)
      mRSpeed = 0;
    if (mLSpeed > MAX_SPEED)
      mLSpeed = MAX_SPEED;
    if (mRSpeed > MAX_SPEED)
      mRSpeed = MAX_SPEED;

    robot->SetMotors(mLSpeed, mRSpeed);
  }
  else
  {
    robot->SetMotors(MAX_SPEED, MAX_SPEED);
  }
}

void LineFollowController::CheckNodes()
{
  //Serial.println("CHECKNODES");
  bool foundIntersection = false;
  int * aaaa = robot->ReflectanceSensors();
  for(int i = 0; i < 6; i++){
    Serial.print(aaaa[i]);  Serial.print('\t');
  }
  Serial.println("");
  // check line read at left end sensor
  if (robot->ReflectanceSensors()[REFL_SENSOR_LEFT_END] > MIN_LINE_FOUND)
  {
    foundIntersection = true;
//    countL++;
//
//    // increment nodeL if read line at left sensor MIN_CONSEC_COUNT times
////    if (countL == MIN_CONSEC_COUNT)
////    {
//      foundIntersection = true;
////    }
//  } else // 0 countL if no line was found
//  {
//    countL = 0;
  }

  // check line read at right end sensor
  if (robot->ReflectanceSensors()[REFL_SENSOR_RIGHT_END] > MIN_LINE_FOUND)
  {
    foundIntersection = true;
//    countR++;
//
//    // increment nodeR if read line at right sensor MIN_CONSEC_COUNT times
////    if (countR == MIN_CONSEC_COUNT)
////    {
//      foundIntersection = true;
//    //}
//  } else // 0 countR if no line was found
//  {
//    countR = 0;
  }

  if (foundIntersection)
  {
    Serial.println("FOUND A INTERSETION");
    CheckToTurn();
  }
}

// This method takes in the value from the 6 sensors, then creates a
// boolean array with value true if that sensor found the line otherwise
// inputs value false. It then sets the isCentered boolean variable to
// true only if either the two middle sensors detected and the number of
// lines detected is less
boolean LineFollowController::Centered()
{
  boolean isCentered;
  int * sensors = robot->ReflectanceSensors();
  // Sets isCentered boolean to true if only either the middle two sensors
  // find the line otherwise boolean value of false is set.
  if ((sensors[0] < MIN_LINE_FOUND and sensors[5] < MIN_LINE_FOUND and
       sensors[1] < MIN_LINE_FOUND and sensors[4] < MIN_LINE_FOUND) and
      (sensors[REFL_SENSOR_LEFT_MIDDLE] > MIN_LINE_FOUND or
       sensors[REFL_SENSOR_RIGHT_MIDDLE] > MIN_LINE_FOUND))
  {
    isCentered = true;
  }
  else
  {
    isCentered = false;
  }
  return isCentered;
}

void LineFollowController::CheckToTurn()
{
  Serial.println(mapArray);
  if (currTurn < strlen(mapArray))
  {
    char turnDirection = mapArray[currTurn];
    if (turnDirection == TURN_LEFT)
    {
      Serial.println("Turning left...");
      currTurn++;
      robot->SetMotors(-100, 150);
      WaitWhileTurning();
    }
    else if (turnDirection == TURN_RIGHT)
    {
      Serial.println("Turning right...");
      currTurn++;
      robot->SetMotors(150, -100);
      WaitWhileTurning();
    }
    else if (turnDirection == MOVE_FORWARD)
    {
      Serial.println("Going straight...");
      currTurn++;
    }
    else if (turnDirection == ROOM_ON_LEFT)
    {
      Serial.println("Entering room...");
      currTurn++;
      robot->SetMotors(200, 200);
      delay(3000);
      robot->SetState(1);
    }
    else
    {
      Serial.println("UNRECOGNIZED COMMAND: ");
    }
  } else {
    Serial.println("Finished turning instructions!");
  }
}

void LineFollowController::WaitWhileTurning()
{
  bool fullyTurned = false;

  while (!fullyTurned)
  {
    if (Centered())
    {
      fullyTurned = true;
    }
  }

  Serial.println("Turning Completed!");

  // reset motor speeds to maxSpeed
  robot->SetMotors(MAX_SPEED, MAX_SPEED);
}
