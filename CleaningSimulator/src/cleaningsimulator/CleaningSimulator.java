/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.*;
import javax.swing.*;
import java.util.Scanner;

/**
 *
 * @author kram1
 */
public class CleaningSimulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        WindowFrame frame = new WindowFrame();

        // Use the setSize method that our BasicFrame
        // object inherited to make the frame
        // 200 pixels wide and high.
        frame.setSize(1000,1000);

        // Make the window show on the screen.
        frame.setVisible(true);
        frame.addKeyListener(new KeyHandler(frame));
        
        Robot r = new Robot(50, 50, 20, frame);
        Room room = new Room(r, frame);
        long last = System.currentTimeMillis();
        int factor = 4;
        while (true)
        {
            if (last + 10*factor < System.currentTimeMillis())
            {
                r.update();
                room.update();
                if (frame.upKey)
                    r.speed += .03*factor;
                else if (frame.downKey)
                    r.speed -= .03*factor;
                else if (r.speed > 0)
                    r.speed -= .05*factor;
                
                if (frame.leftKey != frame.rightKey)
                {
                    if (frame.leftKey)
                        r.dir -= .02*factor;
                    else if (frame.rightKey)
                        r.dir += .02*factor;
                }
                last = System.currentTimeMillis();
                frame.repaint();
            }
        }
    }
    
}
