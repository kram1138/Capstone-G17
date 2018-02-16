#ifndef Robot_H
#define Robot_H

#include "Arduino.h"
#include <ZumoReflectanceSensorArray.h>
#include <ZumoMotors.h>
#include <QTRSensors.h>
#include <Pushbutton.h>

static const int NUM_OF_SENSORS = 6;// number of reflectance sensors
static const int COL_SENSOR_PIN = 3;// pin the buttons of the collision sensor are attached to
static const int DIST_SENSOR_PIN_R1 = A1;
static const int DIST_SENSOR_PIN_R2 = A4;
static const int DIST_SENSOR_PIN_F = A5;

class Robot
{
  public:
    Robot(int newThreshold);
    
    void Navigate();
    void SetMotors(int l, int r);
    void Initialize();
    float FrontSensor();
    float RightSensor1();
    float RightSensor2();
    bool CollisionSensor();
    int GetState();
    void SetState(int newState);
    int ReflectanceSensorPos();
    //bool OnLine();
    int * ReflectanceSensors();
    bool collisionFlag;
  private:
    ZumoMotors * motors;
    ZumoReflectanceSensorArray * reflectanceSensors;
    int state;
    int threshold;
    unsigned int sensors[NUM_OF_SENSORS];// array to store values from reflectance sensors
};

#endif
