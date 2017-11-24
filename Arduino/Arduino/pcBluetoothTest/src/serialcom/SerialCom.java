package serialcom;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * @author Lucas Wiebe-Dembowski
 */
public final class SerialCom {
    
    private final SerialPort serialPort;
    
//    private String STOP = Integer.toString(0x17);
    public String STOP = "\r\n";
    
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
            serialPort.writeBytes(STOP.getBytes());
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
    
    public String listPorts(){
        String result = "";
        String[] portNames = SerialPortList.getPortNames();
        for (String port : portNames) {
            result += port + ", ";
        }
        return result;
    }
    
    public String getPortName(){
        return serialPort.getPortName();
    }
}
