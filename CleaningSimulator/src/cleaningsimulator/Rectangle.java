/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author kram1
 */
public class Rectangle extends SimObject
{
    public Point pt1;
    public Point pt2;
    public Color color;
    
    public Rectangle(Point p1, Point p2, Color color)
    {
        pt1 = p1;
        pt2 = p2;
        this.color = color;
    }
    
    public void paint(Graphics g)
    {
        g.setColor(color);
        int x,y,width,height;
        if (pt1.x < pt2.x)
            x = pt1.x;
        else
            x = pt2.x;
        
        if (pt1.y < pt2.y)
            y = pt1.y;
        else
            y = pt2.y;
        
        width = Math.abs(pt2.x - pt1.x);
        height = Math.abs(pt2.y - pt1.y);
        g.fillRect(x,y,width,height);
    }
}
