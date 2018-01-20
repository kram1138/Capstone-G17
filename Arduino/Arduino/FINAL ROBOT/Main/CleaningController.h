#include "Robot.h"
#include "Arduino.h"

class CleaningController
{
  public:
    CleaningController(Robot * newRobot, int newThreshold);
    
    void Clean();
  private:
    Robot * robot;
    int threshold;
    int cleanState;
    int dir;
};
