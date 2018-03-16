#include "Bluetooth.h"

Bluetooth::Bluetooth(int baud)
{
  baudRate = baud;
  incCmd = false;
  bData = malloc(IN_STR_BUFFER);
  inIndex = 0;
}

char * Bluetooth::LoadMap()
{
  /*String mapData = "";

  snprintf(printStr, PRINT_STR_BUFFER, "Starting loop to receive data...\n");
  Serial.println(printStr);
  
//  mapData = GetMapData();
//  Serial.println(mapData);
//  Serial.println(mapData.length());
  char * ret = malloc(mapData.length()+1);
  /*strcpy(ret, mapData.c_str());
  Serial.println(ret);*/
  ReadCmdFromBluetooth(true);
  Serial.println(bData);
  return bData;
}
String Bluetooth::GetMapData()
{
  String mapData = "";
  
  mapData = GetMapDataString();

  return mapData;
}

String Bluetooth::GetMapDataString()
{
  String mapData = "";
  do
  {
    mapData = ReadCmdFromBluetooth(false);
  } while (!(mapData.length() > 0));
  Serial.println(mapData);
  return mapData;
}

// This method reads each character from the bluetooth serial every 300ms
// while there are bytes available in input buffer or "enter" (0x0D) is
// read. It stores the characters read in String called bData which gets
// returned after it is trimmed and printed to bluetooth serial.
char * Bluetooth::ReadCmdFromBluetooth(bool printBData)
{
  while (Serial.available() == 0);
  
  byte ch = Serial.read();
  while (Serial.available() > 0 && ch != '\n' && ch != '\r')
  {
    bData[inIndex] = ch;
    ch = Serial.read();
    inIndex++;
  }

  bData[inIndex] = '\0';
  
  /*
  char temp;

  if (!incCmd)
  {
    bData = "";
    incCmd = true;
  }

  while (Serial.available() > 0)
  {
    temp = Serial.read();//gets one byte from serial buffer
    if (temp == '\n') {// full command received after ETB (0x17) is read
      incCmd = false;
      break;
    }//breaks out of capture loop when enter is pressed

    bData += temp;

  }//makes the string bData

  if (bData != "" and !incCmd)
  {
    bData.trim();

    if (printBData)
    {
      snprintf(printStr, PRINT_STR_BUFFER, "bData: %s", bData.c_str());
      Serial.println(printStr);
    }
    return bData;
  }
  else
  {
    return (String)"";
  }*/
}

void Bluetooth::SplitByDelimiterIntoArray(String str, String *strArr, char delimiter, const int strArrSize)
{
  int index = 0;
  int prevIndex = 0;
  int strIndex = 0;

  do {
    index = str.indexOf(delimiter);

    if (index != -1) {
      strArr[strIndex] = str.substring(prevIndex, index);
      str = str.substring(index + 1);
    } else {
      strArr[strIndex] = str.substring(0);
      str = "";
    }

    strIndex++;
  } while (str.length() > 0 and strIndex < strArrSize);
}
