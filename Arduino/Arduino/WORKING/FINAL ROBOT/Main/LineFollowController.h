#include "Robot.h"
#include "Bluetooth.h"
#include "Arduino.h"

static const int REFL_SENSOR_LEFT_END = 0;// sensors array position of the left most reflectance sensor
static const int REFL_SENSOR_LEFT_MIDDLE = 2;// sensors array position of the left center reflectance sensor
static const int REFL_SENSOR_RIGHT_END = 5;// sensors array possition of the right most reflectance sensor
static const int REFL_SENSOR_RIGHT_MIDDLE = 3;// sensors array possition of the right center reflectance sensor
static const int MAX_SPEED = 150;// max allowed speed for the motors to turn. Top speed is 400.
static const int MIN_LINE_FOUND = 750;// min value from refletance sensor to confirm line is found
static const int MIN_CONSEC_COUNT = 3;// min consecutive times line read to eliminate false positive.
static const char MOVE_FORWARD = 'f';
static const char TURN_LEFT = 'l';
static const char TURN_RIGHT = 'r';
static const char ROOM_ON_LEFT = 'a';
static const char ROOM_ON_RIGHT = 'd';

class LineFollowController
{
  public:
    LineFollowController(Robot * newRobot);
    void Navigate();
    void Init(char * newMap);
  private:
    void CheckNodes();
    boolean Centered();
    void CheckToTurn();
    void WaitWhileTurning();
    boolean foundWhiteSpace();
    
    Robot * robot;
    char * mapArray;
    int lastError;
    int countR;
    int countL;
    int currTurn;
};
