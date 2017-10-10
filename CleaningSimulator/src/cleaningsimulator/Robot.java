/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author kram1
 */
public class Robot extends SimObject
{
    public float x;
    public float y;
    
    public float dir;
    public float speed;
    
    public int radius;
    
    public Robot(int x, int y, int radius, WindowFrame frame)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        frame.toDraw.add(this);
    }
    
    public void paint(Graphics g)
    {
        g.setColor(Color.MAGENTA);
        g.fillOval((int)x - radius, (int)y - radius, radius * 2, radius * 2);
        g.setColor(Color.BLACK);
        g.drawLine((int)x, (int)y, (int)(x + Math.cos(dir) * radius), (int)(y + Math.sin(dir) * radius));
    }
    
    public void update()
    {
        if (speed > 1*4)
            speed = 1*4;
        else if (speed < -1*4)
            speed = -1*4;
        
        x += Math.cos(dir) * speed;
        y += Math.sin(dir) * speed;
    }
}
