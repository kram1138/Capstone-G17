#include "Arduino.h"
#define LED_PIN 13
class LED
{
  public:
    LED();
    void Blink(int period);
  private:
    unsigned long lastTime;
    int state;
};
