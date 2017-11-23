package pcbluetoothtest;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
 
public class PcBluetoothTest {
   
   public static void main(String[] args) {
      // get computer serial ports names
      String[] portNames = SerialPortList.getPortNames();
      for (String port : portNames) {
         System.out.println(port);
      }
 
      // inicialization with selecting port for communication
      SerialPort serialPort = new SerialPort("COM3");
 
      try {
         // open port for communication
         serialPort.openPort();
         // baundRate, numberOfDataBits, numberOfStopBits, parity
         serialPort.setParams(9600, 8, 1, 0);
         // byte data transfer
         serialPort.writeBytes("stop".getBytes());
         serialPort.writeByte((byte)0x0D);
         // close port
         serialPort.closePort();
      } catch (SerialPortException ex) {
         System.out.println(ex);
      }
   }
}
