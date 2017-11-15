#include <SoftwareSerial.h>// import the serial library

SoftwareSerial mySerial(0, 1); // RX, TX
int ledpin = 13; // led on D13 will show blink on / off
int BluetoothData; // the data given from Computer

void setup() {
  mySerial.begin(9600);
  mySerial.println("Bluetooth Connected:\nPress 1 or 0 to turn on or off LED");
  pinMode(ledpin,OUTPUT);
}

void loop() {

  // check if 
  if (mySerial.available()){
    
    BluetoothData = mySerial.read();

    // Turn on LED if 1 is received
    if(BluetoothData == '1'){
     digitalWrite(ledpin,1);
     mySerial.println("LED On D13 ON !");
    }

    // Turn off LED if 0 is received
    if (BluetoothData == '0'){
     digitalWrite(ledpin,0);
     mySerial.println("LED On D13 Off !");
    }
  }

  // delay till next data
  delay(100);
}
