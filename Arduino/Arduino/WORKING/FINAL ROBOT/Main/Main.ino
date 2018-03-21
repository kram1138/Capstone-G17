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

const unsigned int cleaningTime = 15;
const unsigned int wallFollowTimeout = 60;
const unsigned int wallFollowResetTime = 15;

const unsigned int NAVIGATING = 0;
const unsigned int CLEANING = 1;
const unsigned int LEAVING = 2;

const unsigned int FOLLOWING = 0;
const unsigned int SEARCHING = 1;
const unsigned int BACKING = 2;
const unsigned int DONE = 3;

void setup()
{
  pinMode(13, OUTPUT);
  pinMode(A1, INPUT);
  pinMode(A4, INPUT);
  pinMode(A5, INPUT);
  
  button.waitForButton();
  Serial.begin(38400);// set bluetooth serial baud rate of 38400
  Serial.println("Bluetooth Communication Established!");
  button.waitForButton();

  lineController.Init(bluetooth.LoadMap());
  
  // Turn on LED to indicate we are in calibration mode
  digitalWrite(13, HIGH);
  
  robot.Initialize();
  pinMode(COL_SENSOR_PIN, INPUT);

  //Turn off LED to indicate we are through with calibration
  digitalWrite(13, LOW);

  button.waitForButton();

  startTime = 0;
}

void loop()
{
  if (robot.GetState() == NAVIGATING)
  {
    lineController.Navigate();
    if (robot.GetState() != NAVIGATING)
    {
      Serial.println("Changed state from Navigating!");
      startTime = millis();
    }
//    led.Blink(3000);
  }
  if (robot.GetState() == CLEANING)
  {    
    cleaningController.Clean();
    if ((millis() - startTime)/ 1000 > cleaningTime)
    {
      robot.SetState(LEAVING);
      startTime = millis();
    }
    led.Blink(1000);
  }
  
  if (robot.GetState() == LEAVING)
  {
    if (wallController.GetState() == FOLLOWING || wallController.GetState() == BACKING)
    {
      wallController.FollowWall();
      if ((millis() - startTime) / 1000 > wallFollowTimeout)
      {
        //wallController.SetState(SEARCHING);
        startTime = millis();
      }
      led.Blink(300);
    }
    
    else if (wallController.GetState() == SEARCHING)
    {
      cleaningController.Clean();
      if ((millis() - startTime) / 1000 > wallFollowResetTime
        && (robot.FrontSensor() < 50 || robot.RightSensor1() < 50)
      )
      {
        wallController.SetState(FOLLOWING);
        startTime = millis();
      }
      led.Blink(50);
    }
    else if (wallController.GetState() == DONE)
    {
      Serial.println("Doing nav");
      robot.SetState(NAVIGATING);
    }
  }
}
