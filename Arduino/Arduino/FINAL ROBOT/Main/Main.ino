#include <math.h>
#include "Robot.h"
#include "CleaningController.h"
#include "WallFollowController.h"
#include "LineFollowController.h"
#include "LED.h"
#include "Bluetooth.h"

#define LED_PIN 13

Pushbutton button(ZUMO_BUTTON);

Robot robot(20);
Bluetooth bluetooth(38400);
CleaningController cleaningController(&robot, 20);
WallFollowController wallController(&robot);
LineFollowController lineController(&robot);
LED led;


unsigned long startTime;

const unsigned int cleaningTime = 30;
const unsigned int wallFollowTimeout = 60;
const unsigned int wallFollowResetTime = 15;

void setup()
{
  pinMode(13, OUTPUT);
  
//  button.waitForButton();
  Serial.begin(38400);// set bluetooth serial baud rate of 38400
  Serial.println("Bluetooth Communication Established!");
  button.waitForButton();

  lineController.Init(bluetooth.LoadMap());
  
  // Turn on LED to indicate we are in calibration mode
  digitalWrite(13, HIGH);
  
  robot.Initialize();

  //Turn off LED to indicate we are through with calibration
  digitalWrite(13, LOW);

  button.waitForButton();

  startTime = 0;
}

void loop()
{
  if (robot.GetState() == 0)
  {
    lineController.Navigate();
//    led.Blink(3000);
  } else if (robot.GetState() == 1)
  {
    cleaningController.Clean();
    if ((millis() - startTime)/ 1000 > cleaningTime)
    {
      robot.SetState(2);
      startTime = millis();
    }
    led.Blink(1000);
  }
  else if (robot.GetState() == 2)
  {
    if (wallController.GetState() == 0)
    {
      wallController.FollowWall();
      if ((millis() - startTime) / 1000 > wallFollowTimeout)
      {
        wallController.SetState(1);
        startTime = millis();
      }
      led.Blink(300);
    }
    else
    {
      cleaningController.Clean();
      if ((millis() - startTime) / 1000 > wallFollowResetTime
        && (robot.FrontSensor() < 50 || robot.RightSensor1() < 50)
      )
      {
        wallController.SetState(0);
        startTime = millis();
      }
      led.Blink(50);
    }
  }
//  delay(10);
}
