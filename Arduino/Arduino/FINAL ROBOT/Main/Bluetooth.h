#ifndef BT_H
#define BT_H
#include "Arduino.h"

const char ETB = 0x17;// END OF TRANSMISSION BLOCK 0x0D;
static const int PRINT_STR_BUFFER = 256;// print string buffer size
static const int IN_STR_BUFFER = 256;// print string buffer size
static const char * DELIMITER_SPACE = {' '};
static const char * DELIMITER_UNDERSCORE = {'_'};

class Bluetooth
{
  public:
    Bluetooth(int baud);
    char * LoadMap();
  private:
    int baudRate;
    boolean incCmd;
    int inIndex;
    char * bData;
    char printStr[PRINT_STR_BUFFER];// char array buffer for writing msgs to serial
    String GetMapData();
    String GetMapDataString();
    char * ReadCmdFromBluetooth(bool printBData);
    void Bluetooth::SplitByDelimiterIntoArray(String str, String *strArr, char delimiter, const int strArrSize);
};

#endif
