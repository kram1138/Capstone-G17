#include "Arduino.h"
#include "LED.h"

LED::LED()
{
  lastTime = 0;
  state = 0;
}

void LED::Blink(int period)
{
  if (millis() - lastTime > period)
  {
    state = (state+1) % 2;
    lastTime = millis();
  }
  digitalWrite(LED_PIN, state);
}

