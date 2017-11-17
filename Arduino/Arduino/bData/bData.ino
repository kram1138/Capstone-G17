#include <SoftwareSerial.h>// import the serial library

SoftwareSerial mySerial(0, 1); // RX, TX
String readString;

void setup() {
  mySerial.begin(9600);
  mySerial.println("serial-delimit-21"); // so I can keep track of what is loaded
}

void loop() {

  //expect a string like wer,qwe rty,123 456,hyre kjhg,
  //or like hello world,who are you?,bye!,
  while (mySerial.available()) {
    char c = mySerial.read();  //gets one byte from serial buffer
    if (c == ',') {
      break;
    }  //breaks out of capture loop to print readstring
    readString += c; 
    
    delay(300);  //small delay to allow input buffer to fill
  } //makes the string readString  

  if (readString.length() > 0) {
    mySerial.println(readString); //prints string to serial port out

    readString=""; //clears variable for new input
  }
}
