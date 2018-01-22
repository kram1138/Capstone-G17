#include "Arduino.h"
#include "Robot.h"
#include "WallFollowController.h"

WallFollowController::WallFollowController(Robot * newRobot)
{
  robot = newRobot;
  state = 0;
}

void WallFollowController::FollowWall()
{
  float s1 = robot->RightSensor1();
  float s2 = robot->RightSensor2();
  float s3 = robot->FrontSensor();
  float theta = atan(s2/s1);
  float d = s1*sin(theta);
  
  if (s1 < 100 && s2 < 100 && s3 > 15)
  {
      float angle = (theta * 180/PI)-45;
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
  else if (s1 < 100 && s2 < 100 && s3 <= 15)
  {
    robot->SetMotors(-150, 150);
    delay(350);
    robot->SetMotors(0, 0);
  }
  else if (s1 >= 100 && s2 < 100)
  {
    robot->SetMotors(200, 100);
  }
  else
  {
    robot->SetMotors(150, 150);
  }
}

int WallFollowController::GetState()
{
  return state;
}
void WallFollowController::SetState(int newState)
{
  state = newState;
}
