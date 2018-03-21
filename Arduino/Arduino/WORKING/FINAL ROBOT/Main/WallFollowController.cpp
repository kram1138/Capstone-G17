#include "Arduino.h"
#include "Robot.h"
#include "WallFollowController.h"

const unsigned int FOLLOWING = 0;
const unsigned int SEARCHING = 1;
const unsigned int BACKING = 2;
const unsigned int DONE = 3;

WallFollowController::WallFollowController(Robot * newRobot)
{
  robot = newRobot;
  state = 0;
  startTime = 0;
}

void WallFollowController::FollowWall()
{
  Serial.print("Right1: ");
  Serial.print(robot->RightSensor1());
  Serial.print("\tRight2: ");
  Serial.print(robot->RightSensor2());
  Serial.print("\tFront: ");
  Serial.println(robot->FrontSensor());
  float theta = atan(robot->RightSensor2()/robot->RightSensor1());
  float d = robot->RightSensor1()*sin(theta);
  
  int * sensors = robot->ReflectanceSensors();
  if (sensors[0] > 750 || sensors[1] > 750 || sensors[2] > 750 || sensors[3] > 750 || sensors[4] > 750 || sensors[5] > 750)
  {
    Serial.println("Found line");
    state = DONE;
  }
  
  if (robot->CollisionSensor())
  {
    Serial.println("Backing up");
    state = BACKING;
    startTime = millis();
  }
  
  if (state == FOLLOWING)
  {
    float angle = (theta * 180/PI)-45;
    if (robot->RightSensor1() < 30 && robot->RightSensor2() < 30 && robot->FrontSensor() > 15)
    {
        if (d < 15)
        {
          if ((angle > -15 && d < 10) || angle > 15)
          {
            robot->SetMotors(100, 200);
          }
          else
          {
            if (100+(20+angle)*10 < 200 && 100+(20+angle)*10 > 0)
              robot->SetMotors(200, 100+(20+angle)*10);
            else if (100+(20+angle)*10 >= 200)
              robot->SetMotors(200, 200);
            else
              robot->SetMotors(200, 0);
          }
        }
        else
        {
          if ((angle < 15 && d > 20) || angle < -15)
          {
              robot->SetMotors(200, 100);
          }
          else
          {
            if (100+(20-angle)*10 < 200 && 100+(20-angle)*10 > 0)
              robot->SetMotors(100+(20-angle)*10, 200);
            else if (100+(20-angle)*10 >= 200)
              robot->SetMotors(200, 200);
            else
              robot->SetMotors(0, 200);
          }
        }
    }
    else if (robot->FrontSensor() <= 15)
    {
      robot->SetMotors(-150, 150);
      delay(350);
      robot->SetMotors(0, 0);
    }
    else if (robot->RightSensor1() >= 30)
    {
      robot->SetMotors(200, 100);
    }
    else
    {
      robot->SetMotors(150, 150);
    }
  }
  else if (state == BACKING)
  {
      robot->SetMotors(-120,-180);
      if (millis() - startTime > 1000)
      {
        state = FOLLOWING;
      }
  }
  return false;
}

int WallFollowController::GetState()
{
  return state;
}
void WallFollowController::SetState(int newState)
{
  state = newState;
}
