package pcbluetoothtest;
 
import java.util.Arrays;

/**
 * @author Lucas Wiebe-Dembowski
 */
public class PcBluetoothTest {
    final static boolean USING_GUI = true;
   
    public static void main(String[] args) {
        serialcom.SerialCom myCom = new serialcom.SerialCom("COM5");
        
        if(USING_GUI){
            final netbeansgui.GUI myUI = new netbeansgui.GUI();
            
            usercommandhandler.UserCommandHandler myCommand = new usercommandhandler.UserCommandHandler(myCom);
            myUI.addObserver(myCommand);
            myCommand.addObserver(myUI);
        
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                   myUI.setVisible(true);
                }
            });
            
            myCom.addObserver(myUI);
            myCom.start();
        
        }else{ //broken now, StandardIO needs to extend Observable and implement Observer
            final standardio.StandardIO myUI = new standardio.StandardIO();
            
            usercommandhandler.UserCommandHandler myCommand = new usercommandhandler.UserCommandHandler(myCom);
            myUI.setCommand(myCommand);
        
            Thread theUIThread = new Thread(myUI);
            theUIThread.start();
        }

        System.out.println("UI Thread started.");
   }
}
