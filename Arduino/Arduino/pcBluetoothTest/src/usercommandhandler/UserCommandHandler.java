package usercommandhandler; //For client application

public class UserCommandHandler {
    private userinterface.UserInterface myUI;
    private serialcom.SerialCom myCom;

    public UserCommandHandler(userinterface.UserInterface myUI, serialcom.SerialCom myCom) {
        //pcBluetoothTest INSTANTIATES ONE OF THESE
        this.myUI = myUI;
        this.myCom = myCom;
    }
    
    public void handleUserCommand(String myCommand) {
        //UI CALLS THIS METHOD WHEN USER ENTERS A COMMAND
        switch (myCommand) {
            case "q": //QUIT
                if (myCom.isOpened()) {
                    myCom.close();
                }
                myUI.update("Exiting program");
                System.exit(-1);
                break;
            case "open": //open port
                if(myCom.isOpened()){
                    myUI.update("Port was already open.");
                }else if (myCom.open()) {
                    myUI.update("Successfully opened port.");
                } else {
                    myUI.update("Could not open port");
                }
                break;
            case "close": //close port
                if (myCom.isOpened()) {
                    if(myCom.close()){
                        myUI.update("Successfully closed port.");
                    }else{
                        myUI.update("Could not close port.");
                    }
                 } else {
                     myUI.update("Port was not open.");
                 }                    
                break;
            default:
                myUI.update("command is: '" + myCommand + "'");
                myCom.writeBytes(myCommand);
                break;
        }
    }
}