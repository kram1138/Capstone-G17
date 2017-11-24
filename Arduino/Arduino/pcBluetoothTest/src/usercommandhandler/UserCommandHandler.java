package usercommandhandler; //For client application

/**
 * @author Lucas Wiebe-Dembowski
 */
public class UserCommandHandler {
    static final byte CR = 0x0D;
    static final byte LF = 0x0A;
    
    private final userinterface.UserInterface myUI;
    private final serialcom.SerialCom myCom;

    public UserCommandHandler(userinterface.UserInterface myUI, serialcom.SerialCom myCom) {
        //pcBluetoothTest instantiates a UserCommandHandler
        this.myUI = myUI;
        this.myCom = myCom;
    }
    
    public void handleUserCommand(String myCommand) {
        //UI CALLS THIS METHOD WHEN USER ENTERS A COMMAND
        String[] words = myCommand.split("\\s+");
        switch (words[0]) {
        case "": break;
        case "q": //QUIT
            if (myCom.isOpened()) {
                myCom.close();
            }
            myUI.update("Exiting program");
            System.exit(0);
            break;
        case "open":
            if(myCom.isOpened()){
                myUI.update("Port " + myCom.getPortName() + " was already open");
            }else if (myCom.open()) {
                myUI.update("Successfully opened port " + myCom.getPortName());
            } else {
                myUI.update("Could not open port " + myCom.getPortName());
            }
            break;
        case "close":
            if (myCom.isOpened()) {
                if(myCom.close()){
                    myUI.update("Successfully closed port " + myCom.getPortName());
                }else{
                    myUI.update("Could not close port " + myCom.getPortName());
                }
             } else {
                 myUI.update("No port was open");
             }                    
            break;
        case "stopCode":
            if(words.length > 1 && !words[1].isEmpty()){
                int a = words[1].indexOf("0x");
                if(a >= 0 && words[1].length() >= a + 4){ //valid 1-byte hex number
                      myCom.STOP[0] = Byte.parseByte(words[1].substring(a+2,a+4), 16);
                      myCom.STOP[1] = Byte.parseByte(words[1].substring(a+2,a+4), 16);
                }else if(words[1].equals("CRLF")){ //carriage return + line feed
                      myCom.STOP[0] = CR;
                      myCom.STOP[1] = LF;
                }else{
                    myUI.update("Error: Stop Code must be either CRLF or a single byte hex number entered in the format 0xNN.\n"
                        + "Current Stop Code is " + (myCom.STOP[0] == CR && myCom.STOP[1] == LF ? "CRLF" : myCom.STOP[0]));
                }
                myUI.update("Changed Stop Code to " + words[1]);
            }else{
                myUI.update("To change Stop Code, please enter the new Stop Code in the text box.\n"
                        + "Current Stop Code is " + (myCom.STOP[0] == CR && myCom.STOP[1] == LF ? "CRLF" : myCom.STOP[0]));
            }
            break;
        case "portName":
            if(words.length > 1 && !words[1].isEmpty()){
                myCom.setPortName(words[1]);
                myUI.update("Changed port name to " + myCom.getPortName());
            }else{
                myUI.update("To change port name, please enter the new port name in the text box.\n"
                        + "Current port name is " + myCom.getPortName());
            }
            break;
        case "listPorts":
            myUI.update(myCom.listPorts());
            break;
        default:
            if(myCom.isOpened()){
                myUI.update("command is: '" + myCommand + "'");
                myCom.write(myCommand);
            }else{
                myUI.update("No port open, doing nothing.");
            }
            break;
        }
    }
}
