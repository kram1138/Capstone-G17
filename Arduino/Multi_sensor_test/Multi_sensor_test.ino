/*=========================================================
Infra-Red distance measures: Send distance value in centimeters to serial
Written By Tiago A. on 21 Mar 2015
===========================================================
How to connect the sensor to the Arduino: 
  sensor - arduino
     VCC - 5V
     Gnd - GND
     OUT - A0
     EN  - Do not connect

Briefing:  
This program has the sole purpose of measuring distances with the Infra-Red sensor.
It uses a previous calculated regression to convert analogRead values into distance values.

How to use:
After uploading this program, the Arduino will start to send the distance of the sensor to the object through the serial port.
Don't forget to open the Serial Monitor in the Tools tab for debugging purposes. (Ctrl + Shift + M)
*/
const int pin1 = A0;
const int pin2 = A1;
const int pin3 = A2;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop(){

  
  // Reads the InfraRed sensor analog values and convert into distance.
  int sensorValue1 = analogRead(pin1);
  int sensorValue2 = analogRead(pin2);
  int sensorValue3 = analogRead(pin3);
  
  double IRdistance1 = 187754 * pow(sensorValue1, -1.51);
  double IRdistance2 = 187754 * pow(sensorValue2, -1.51);
  double IRdistance3 = 187754 * pow(sensorValue3, -1.51);

  
  Serial.print(IRdistance1);
  Serial.print("\t");
  Serial.print(IRdistance2);
  Serial.print("\t");
  Serial.println(IRdistance3);
  
  // A delay is added for a stable and precise input
  delay(250);
}
