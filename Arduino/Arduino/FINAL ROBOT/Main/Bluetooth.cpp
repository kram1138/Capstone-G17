#include "Bluetooth.h"

Bluetooth::Bluetooth(int baud)
{
  baudRate = baud;
  incCmd = false;
  bData = "";
}

char * Bluetooth::LoadMap()
{
  String mapData = "";

  snprintf(printStr, PRINT_STR_BUFFER, "Starting loop to receive data...\n");
  Serial.println(printStr);
  
  mapData = GetMapData();

  int spaceIndex = mapData.indexOf(' ');
  int numTurns = mapData.substring(0, spaceIndex).toInt();
  char * mapArr = malloc(sizeof(char) * numTurns + 1);
  String dataArr[numTurns];
  String data = mapData.substring(spaceIndex + 1);// removes turns number from the front

  snprintf(printStr, PRINT_STR_BUFFER, "\nData: %s\n", data.c_str());
  Serial.println(printStr);

  SplitByDelimiterIntoArray(data, dataArr, DELIMITER_SPACE, numTurns);
  snprintf(printStr, PRINT_STR_BUFFER, "Done split by space!\n");
  Serial.println(printStr);

  for (int i = 0; i < numTurns; i++) {
    mapArr[i] = dataArr[i][0];
    snprintf(printStr, PRINT_STR_BUFFER, "Turn%d: %c\n", i+1,mapArr[i]);
    Serial.println(printStr);
  }
  mapArr[numTurns] = '\0';
  return mapArr;
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
String Bluetooth::ReadCmdFromBluetooth(bool printBData)
{
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
  }
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
