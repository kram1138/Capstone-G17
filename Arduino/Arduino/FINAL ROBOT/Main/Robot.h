#ifndef Robot_H
#define Robot_H

#include "Arduino.h"
#include <ZumoReflectanceSensorArray.h>
#include <ZumoMotors.h>
#include <QTRSensors.h>
#include <Pushbutton.h>

static const int NUM_OF_SENSORS = 6;// number of reflectance sensors

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
    int GetState();
    void SetState(int newState);
    int ReflectanceSensorPos();
    int * ReflectanceSensors();
  private:
    ZumoMotors * motors;
    ZumoReflectanceSensorArray * reflectanceSensors;
    int state;
    int threshold;
    unsigned int sensors[NUM_OF_SENSORS];// array to store values from reflectance sensors
};

#endif
