/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import java.util.Scanner;

/**
 *
 * @author kram
 */
public class CleaningSimulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        float speed = 0.5f;
        float pixelsPerMeter = 100;
        Random rand = new Random();
        int sizex = 1600, sizey = 1000;
        WindowFrame frame = new WindowFrame(sizex, sizey);
        // Use the setSize method that our BasicFrame
        // object inherited to make the frame
        // 200 pixels wide and high.
        frame.setSize(sizex, sizey);

        // Make the window show on the screen.
        frame.setVisible(true);
        frame.addKeyListener(new KeyHandler(frame));
        
        Room room = new Room(frame, sizex, sizey, rand);
        Robot r = new Robot(500, 500, 20, frame, room);
        long last = System.currentTimeMillis();
        int frames = 50;
        while (true)
        {
            if (last + frames < System.currentTimeMillis())
            {
                r.update();
                room.update(r);
                if (frame.upKey)
                    r.setSpeed((float)frames/1000*pixelsPerMeter*speed);
                else if (frame.downKey)
                    r.setSpeed(0);
                
                if (frame.leftKey != frame.rightKey)
                {
                    if (frame.leftKey)
                        r.changeDir(0.1f);
                    else if (frame.rightKey)
                        r.changeDir(-.1f);
                }
                last = System.currentTimeMillis();
                frame.repaint();
            }
        }
    }
    
}
