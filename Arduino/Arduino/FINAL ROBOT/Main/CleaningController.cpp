#ifndef CC_H
#define CC_H
#include "Arduino.h"
#include "Robot.h"
#include "CleaningController.h"


CleaningController::CleaningController(Robot * newRobot, int newThreshold)
{
  threshold = newThreshold;
  robot = newRobot;
  cleanState = 0;
  dir = 0;
}

void CleaningController::Clean()
{
  if (cleanState == 0)
  {
    if (robot->FrontSensor() < threshold || robot->RightSensor1() < threshold)
    {
        cleanState = 1;
        if (robot->RightSensor1() < 100 || robot->RightSensor2() < 100)
            dir = 0;
        else
            dir = 1;
    }
    else
    {
      robot->SetMotors(150, 150);
    }
  }
  else if (cleanState == 1)
  {
    if (random(5) == 0 && robot->RightSensor1() > 100 && robot->FrontSensor() > 100)
    {
      cleanState = 0;
    }
    else
    {
      if (dir == 0)
      {
        robot->SetMotors(-150, 150);
      }
      else
      {
        robot->SetMotors(150, -150);
      }
    }
  }
}
#endif
