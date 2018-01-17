package serialcom;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import java.util.Arrays;
import java.util.Observable;

/**
 * @author Lucas Wiebe-Dembowski
 */
public final class SerialCom extends Observable implements Runnable{

    static final byte CR = 0x0D;
    static final byte LF = 0x0A;

    private SerialPort serialPort;
    
//    public byte[] STOP = {CR, LF};
    public byte[] STOP = {0x17, 0x17};
    
    boolean stopThisThread = false;
    
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
                success = success && serialPort.setParams(SerialPort.BAUDRATE_4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);
                // baudRate, numberOfDataBits, numberOfStopBits, parity
            }
        }catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return success;
    }
    
    public boolean CTS(){
        //Check if the port is clear to send. If not, you should not attempt sending anything.
        boolean success = false;
        try{
            success = serialPort.isCTS();
        }catch(SerialPortException ex){
            System.out.println(ex);
        }
        return success;
    }
    
    public boolean write(String message){
        boolean success = false;
        try {
            success = serialPort.writeBytes(message.getBytes());
            success = success && serialPort.writeBytes(STOP);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return success;
    }
    
    public boolean write(byte message){
        boolean success = false;
        try {
            success = serialPort.writeByte(message);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return success;
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
    
    public void notify(String msg) {
        setChanged();
        notifyObservers(msg);
    }
    
    public void start(){
        Thread myClientThread = new Thread(this);
        myClientThread.start();
    }
    
    String data = "";
    @Override
    public void run(){ //read messages from server
        String msgFromServer = "";
        while(stopThisThread == false){
            System.out.print("");
            if(isOpened()){
                try {
                    msgFromServer = serialPort.readString();
                    if(msgFromServer != null){
                        data += msgFromServer;
                    }
                    if(data.contains("\r\n")){
                        notify("Arduino" + data);
                        //"Arduino" indicates to observer that the message came 
                        //from the server, not from this program.
                        data = "";
                    }
                } catch (SerialPortException ex) {
                    if(stopThisThread == false) {
                        System.out.println("Unexpected disconnection from server: please try connecting again.");
                        stopThisThread = true;
                    }
                } 
            }
        }
    }
}
