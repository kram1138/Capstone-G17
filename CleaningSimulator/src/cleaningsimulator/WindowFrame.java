/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author kram1
 */
public class WindowFrame  extends JFrame
{
    public ArrayList<SimObject> toDraw = new ArrayList<SimObject>();
    
    public boolean upKey = false;
    public boolean downKey = false;
    public boolean leftKey = false;
    public boolean rightKey = false;
    
    public WindowFrame()
    {
        super();
    }
    
    public void paint(Graphics g)
    {
        super.paintComponents(g);
        for (SimObject d : toDraw)
        {
            d.paint(g);
        }
    }
}