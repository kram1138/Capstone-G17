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

void changeDirection(){
  int num1 = rand() % 1000;
  moveBackward(200);
  delay(200);
  moveRight(200);
  delay(num1);
  
}
//****************************************************************************


void setup()
{
  pinMode(LED_PIN, OUTPUT);
  Serial.begin(9600);

}

void loop() {
  moveForward(100);
  int sensorValue = analogRead(A0);
  double IRdistance = 187754 * pow(sensorValue, -1.51);

  if (IRdistance < 30){
    delay(1000);
    sensorValue = analogRead(A0);
    IRdistance = 187754 * pow(sensorValue, -1.51);

    if (IRdistance <30){
      moveBackward(200);
      delay(500);
      changeDirection();
    }
  }
  
  delay(100);

}
