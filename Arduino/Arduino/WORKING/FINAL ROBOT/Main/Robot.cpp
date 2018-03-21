#include <ZumoReflectanceSensorArray.h>
#include <ZumoMotors.h>
#include "Robot.h"

Robot::Robot(int newThreshold)
{
  state = 0;
  threshold = threshold;
  ZumoReflectanceSensorArray arr;
  reflectanceSensors = &arr;
  collisionFlag = false;
}

void Robot::Initialize()
{
  // Initialize the reflectance sensors module
  reflectanceSensors->init();

  // Wait 1 second and then begin automatic sensor calibration
  // by rotating in place to sweep the sensors over the line
  delay(1000);
  int i;
  for(i = 0; i < 80; i++)
  {
    if ((i > 10 && i <= 30) || (i > 50 && i <= 70))
      SetMotors(-200, 200);
    else
      SetMotors(200, -200);
    reflectanceSensors->calibrate();

    delay(20);
  }
  SetMotors(0, 0);
}

void Robot::SetMotors(int l, int r)
{
  motors->setSpeeds(l, r);
}

float Robot::FrontSensor()
{
  return pow(analogRead(DIST_SENSOR_PIN_F)/2401.6,1/-.575);
}
float Robot::RightSensor1()
{
  return pow(analogRead(DIST_SENSOR_PIN_R1)/4205.5,1/-.777);
}
float Robot::RightSensor2()
{
  return pow(analogRead(DIST_SENSOR_PIN_R2)/5970.8,1/-.886);
}

int Robot::GetState()
{
  return state;
}
void Robot::SetState(int newState)
{
  state = newState;
}

int Robot::ReflectanceSensorPos()
{
  reflectanceSensors->readCalibrated(sensors);
//  for (int i = 0; i < 6; i++)
//  {
//    Serial.print(sensors[i]);
//    Serial.print("\t");
//  }
//  Serial.println();
  return reflectanceSensors->readLine(sensors);
}

int * Robot::ReflectanceSensors()
{
  reflectanceSensors->readCalibrated(sensors);
//  for (int i = 0; i < 6; i++)
//  {
//    Serial.print(sensors[i]);
//    Serial.print("\t");
//  }
//  Serial.println();
  return sensors;
}

/*bool Robot::OnLine()
{
  int count = 0;
  int i;
  reflectanceSensors->readLine(sensors);
  for (i = 0; i < NUM_OF_SENSORS; i++)
  {
    Serial.print(sensors[i]);
    Serial.print("\t");
  }
  Serial.print("\n");
  //Serial.println();
  for (i = 0; i < NUM_OF_SENSORS; i++)
  {
    if (sensors[i] > 750)
      return true;
  }
  return false;
}*/

bool Robot::CollisionSensor()
{
  bool ret = collisionFlag;
  collisionFlag = false;
  if (ret || digitalRead(COL_SENSOR_PIN))
  {
    Serial.println("Hit something");
    return true;
  }
  return false;
}

