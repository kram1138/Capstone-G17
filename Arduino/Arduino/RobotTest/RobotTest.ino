#include <ZumoMotors.h>

#define LED_PIN 13

ZumoMotors motors;


//**************************************************************************
void moveForward(int speed){
  motors.setLeftSpeed(speed);
  motors.setRightSpeed(speed);
}

void moveBackward(int speed){
  motors.setLeftSpeed(-speed);
  motors.setRightSpeed(-speed);
}

void moveRight(int speed){
  motors.setLeftSpeed(speed);
  motors.setRightSpeed(-speed);
}

void moveLeft(int speed){
  motors.setLeftSpeed(-speed);
  motors.setRightSpeed(speed);
}

void moveStop(){
  motors.setLeftSpeed(0);
  motors.setRightSpeed(0);
  
}

void turn90Right(){
  moveRight(200);
  delay(330);
  moveStop();
}

void turn90Left(){
  moveLeft(200);
  delay(330);
  moveStop();
}
//****************************************************************************


void setup()
{
  pinMode(LED_PIN, OUTPUT);

}

void loop() {

  moveForward(200);
  delay(2000);
  moveStop();
  delay(1000);
  turn90Right();
  delay(1000);
  turn90Right();
  delay(1000);
  turn90Right();
  delay(1000);
  moveBackward(200);
  delay(2000);
  moveStop();
  delay(1000);
  turn90Left();
  delay(1000);
  turn90Left();
  delay(1000);
  turn90Left();
  delay(1000);
  moveForward(400);
  delay(1000);
  moveBackward(400);
  delay(1000);
  

}
