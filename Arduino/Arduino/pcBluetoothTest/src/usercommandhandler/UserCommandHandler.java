package usercommandhandler; //For client application

import java.util.Observable;
import java.util.Observer;


/**
 * @author Lucas Wiebe-Dembowski
 */
public class UserCommandHandler extends Observable implements Observer {
    static final byte CR = 0x0D;
    static final byte LF = 0x0A;
    
    private final serialcom.SerialCom myCom;
    private String myUserCommand;

    public UserCommandHandler(serialcom.SerialCom myCom) {
        //pcBluetoothTest instantiates a UserCommandHandler
        this.myCom = myCom;
    }
    
    public void notify(String msg) {
        setChanged();
        notifyObservers(msg);
    }
    
    @Override
    public void update(Observable obj, Object arg){ //Observer update() method.
        if(arg instanceof String){
            myUserCommand = (String)arg;
            handleUserCommand(myUserCommand);
        }
    }
    
    public void sendMessage(String message){
        if(!myCom.isOpened()){
            sendMessageToUI("Open a port first.");
        }else if(!myCom.CTS()){
            sendMessageToUI("Make sure the other device is connected first.");
        }else{
            sendMessageToUI("Sending "+message+" to robot.");
            if(!myCom.write(message)){ sendMessageToUI("Error sending "+message+" to robot."); }
        }
    }

    public void sendMessageToUI(String theString) {
        notify(theString);
    }
    
    public void handleUserCommand(String myCommand) {
        //update() calls this
        String[] words = myCommand.split("\\s+");
        switch (words[0]) {
        case "": break;
        case "q": //QUIT
            if (myCom.isOpened()) {
                myCom.close();
            }
            sendMessageToUI("Exiting program");
            System.exit(0);
            break;
        case "ACK":
            if(words.length > 1){
                sendMessage(words[1]);
            }else{
                sendMessage("AAAAAAAAAA"); //default, in case observable screws this up
            }
            break;
        case "path":
            String path = "";
            for(int i = 1; i < words.length; i++){
                path += (words[i] + " ");
            }
            sendMessage(path);
            break;
        case "start":
            sendMessage("start");
            break;
        case "stop":
            sendMessage("stop");
            break;
        case "open":
            if(myCom.isOpened()){
                sendMessageToUI("Port " + myCom.getPortName() + " was already open");
            }else if (myCom.open()) {
                sendMessageToUI("Successfully opened port " + myCom.getPortName());
            } else {
                sendMessageToUI("Could not open port " + myCom.getPortName());
            }
            break;
        case "close":
            if (myCom.isOpened()) {
                if(myCom.close()){
                    sendMessageToUI("Successfully closed port " + myCom.getPortName());
                }else{
                    sendMessageToUI("Could not close port " + myCom.getPortName());
                }
            } else {
                sendMessageToUI("No port was open");
            }                    
            break;
        case "stopCode":// CHANGED THIS
            if(words.length > 1 && !words[1].isEmpty()){
                int a = words[1].indexOf("0x");
                if(a >= 0 && words[1].length() >= a + 4){ //valid 1-byte hex number
                    byte[] theByte = {Byte.parseByte(words[1].substring(a+2,a+4), 16),};
                    myCom.STOP[0] = theByte[0];
                    myCom.STOP[1] = theByte[0];
                    myCom.STOPBYTE = new String(theByte);
                }else if(words[1].equalsIgnoreCase("CRLF")){ //carriage return + line feed
                    myCom.STOP[0] = CR;
                    myCom.STOP[1] = LF;
                    myCom.STOPBYTE = "\n";
                }else{
                    sendMessageToUI("Error: Stop Code must be either CRLF or a single byte hex number entered in the format 0xNN.\n"
                        + "Current Stop Code is " + (myCom.STOP[0] == CR && myCom.STOP[1] == LF ? "CRLF" : myCom.STOP[0]));
                }
                sendMessageToUI("Stop Code set to " + words[1]);
            }else{
                sendMessageToUI("To change Stop Code, please enter the new Stop Code in the text box.\n"
                        + "Current Stop Code is " + (myCom.STOP[0] == CR && myCom.STOP[1] == LF ? "CRLF" : myCom.STOP[0]));
            }
            break;
        case "portName":
            if(words.length > 1 && !words[1].isEmpty()){
                myCom.setPortName(words[1]);
                sendMessageToUI("Port name set to " + myCom.getPortName());
            }else{
                sendMessageToUI("To change port name, please enter the new port name in the text box.\n"
                        + "Current port name is " + myCom.getPortName());
            }
            break;
        case "listPorts":
            sendMessageToUI(myCom.listPorts());
            break;
        default:
            break;
        }
    }
}


/*
lr
rr
li
ri

path 3 li_2 ri_3 ri_4
18 li_1 li_2 li_3 li_4 li_5 li_6 li_7 li_8 li_9 ri_1 ri_2 ri_3 ri_4 ri_5 ri_6 ri_7 ri_8 ri_9
li_1 li_2 li_3 li_4 li_5 li_6 li_7 li_8 li_9 li_10 li_11 li_12 li_13 li_14 li_15 li_16 li_17 li_18 li_19 li_20 li_21 li_22 li_23 li_24 li_25 li_26 li_27 li_28 li_29 li_30 li_31 li_32 li_33 li_34 li_35 li_36 li_37 li_38 li_39 li_40


40 l r a d l r a d l r a d l r a d l r a d l r a d l r a d l r a d l r a d l r a d

*/
