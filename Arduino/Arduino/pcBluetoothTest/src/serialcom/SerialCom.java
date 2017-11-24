package serialcom;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import java.util.Arrays;

/**
 * @author Lucas Wiebe-Dembowski
 */
public final class SerialCom {

    static final byte CR = 0x0D;
    static final byte LF = 0x0A;

    private SerialPort serialPort;
    
//    public byte[] STOP = {CR, LF};
    public byte[] STOP = {0x17, 0x17};
    
    public SerialCom(String portName){
        serialPort = new SerialPort(portName);
    }
    
    public boolean isOpened(){
        return serialPort.isOpened();
    }
    
    public boolean close(){
        boolean success = false;
        try{
            if(serialPort.isOpened()){
                success = serialPort.closePort();
            }
        }catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return success;
    }
    
    public boolean open(){
        boolean success = false;
        try{
            if(!serialPort.isOpened()){
                success = serialPort.openPort(); // open port for communication
                success = success && serialPort.setParams(115200, 8, 0, 0); // baundRate, numberOfDataBits, numberOfStopBits, parity
            }
        }catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return success;
    }
    
    public void write(String message){
        try {
            serialPort.writeBytes(message.getBytes());
            serialPort.writeBytes(STOP);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    public void write(byte message){
        try {
            serialPort.writeByte(message);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    public String listPorts(){
        return Arrays.toString(SerialPortList.getPortNames());
    }
    
    public String getPortName(){
        return serialPort.getPortName();
    }
    
    public boolean setPortName(String portName){
        boolean success = false;
        serialPort = new SerialPort(portName);
        return success;
    }
}
