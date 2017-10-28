#include <ZumoMotors.h>



#define LED_PIN 13

ZumoMotors motors;

void setup()
{
  pinMode(LED_PIN, OUTPUT);

}

void loop()
{
  motors.setLeftSpeed(50);
  motors.setRightSpeed(50);

  delay(1000);

  motors.setLeftSpeed(0);
  motors.setRightSpeed(0);

  delay(1000);
}

