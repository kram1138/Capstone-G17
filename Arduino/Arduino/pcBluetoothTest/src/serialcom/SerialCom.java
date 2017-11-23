package serialcom;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author Lucas Wiebe-Dembowski
 */
public final class SerialCom {
    
    private SerialPort serialPort;
    
    private final byte STOP = (byte)0x17;
    public byte getSTOPCode(){ return STOP; }
    
    public SerialCom(){
        this("COM6");
    }
    public SerialCom(String portName){
        serialPort = new SerialPort(portName);
//        open();
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
                success = success && serialPort.setParams(115200, 8, 1, 0); // baundRate, numberOfDataBits, numberOfStopBits, parity
            }
        }catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return success;
    }
    
    public void writeBytes(String message){
        try {
            serialPort.writeBytes(message.getBytes());
            serialPort.writeByte(STOP);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    public void writeByte(byte message){
        try {
            serialPort.writeByte(message);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
}
