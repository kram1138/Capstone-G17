#include "Robot.h"
#include "Arduino.h"

class WallFollowController
{
  public:
    WallFollowController(Robot * newRobot);
    
    void FollowWall();
    int GetState();
    void SetState(int newState);
  private:
    Robot * robot;
    int state;
    unsigned long startTime;
};
