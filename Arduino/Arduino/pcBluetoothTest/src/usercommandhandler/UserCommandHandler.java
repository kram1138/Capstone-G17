package usercommandhandler; //For client application

/**
 * @author Lucas Wiebe-Dembowski
 */
public class UserCommandHandler {
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
                if(a >= 0 && words[1].length() >= a + 4){
                    myCom.STOP = words[1].substring(a, a+4);
                }else if(words[1].equals("CRLF")){
                    myCom.STOP = "\r\n";
                }else{
                    myCom.STOP = words[1];
                }
            }else{
                myUI.update("setStopCode usage: setStopCode [code].\nCurrent Stop Code is " + myCom.STOP);
            }
            break;
        case "portName":
            if(words.length > 1 && !words[1].isEmpty()){
                int a = words[1].indexOf("0x");
                if(a >= 0 && words[1].length() >= a + 4){
                    myCom.STOP = words[1].substring(a, a+4);
                }else if(words[1].equals("CRLF")){
                    myCom.STOP = "\r\n";
                }else{
                    myCom.STOP = words[1];
                }
            }else{
                myUI.update("portName usage: portName [name].\nCurrent portName is " + myCom.getPortName());
            }
            break;
        case "listPorts":
            myUI.update(myCom.listPorts());
            break;
        default:
            if(myCom.isOpened()){
                myUI.update("command is: '" + myCommand + "'");
                myCom.writeBytes(myCommand);
            }else{
                myUI.update("No port open, doing nothing.");
            }
            break;
        }
    }
}
