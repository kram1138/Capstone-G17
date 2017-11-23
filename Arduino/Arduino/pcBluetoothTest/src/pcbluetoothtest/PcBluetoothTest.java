package pcbluetoothtest;


import jssc.SerialPortList;
 
public class PcBluetoothTest {
   
    public static void main(String[] args) {
        // get computer serial ports names
        String[] portNames = SerialPortList.getPortNames();
        for (String port : portNames) {
            System.out.println(port);
        }
      
        serialcom.SerialCom myCom = new serialcom.SerialCom("COM6");
        standardio.StandardIO myUI = new standardio.StandardIO();
        usercommandhandler.UserCommandHandler myCommand = new usercommandhandler.UserCommandHandler(myUI, myCom);
        myUI.setCommand(myCommand);
        Thread theUIThread = new Thread(myUI);
        theUIThread.start();
        System.out.println("UI Thread started.");
   }
}
