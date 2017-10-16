/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.Color;
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
    
    public int sizex;
    public int sizey;
    
    public boolean upKey = false;
    public boolean downKey = false;
    public boolean leftKey = false;
    public boolean rightKey = false;
    public boolean hKey = false;
    
    public WindowFrame(int x, int y)
    {
        super();
        sizex = x;
        sizey = y;
    }
    
    public void paint(Graphics g)
    {
        super.paintComponents(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, sizex, sizey);
        for (SimObject d : toDraw)
        {
            d.paint(g);
        }
    }
}