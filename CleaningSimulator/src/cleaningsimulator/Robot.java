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
public class Robot extends SimObject
{
    private PointF loc;
    
    private float dir;
    private float speed;
    
    public int radius;
    private Room room;
    
    public Robot(int x, int y, int radius, WindowFrame frame, Room room)
    {
        loc = new PointF(x,y);
        this.radius = radius;
        frame.toDraw.add(this);
        this.room = room;
    }
    
    public void paint(Graphics g)
    {
        g.setColor(Color.MAGENTA);
        g.fillOval((int)loc.x - radius, (int)loc.y - radius, radius * 2, radius * 2);
        g.setColor(Color.BLACK);
        g.drawLine((int)loc.x, (int)loc.y, (int)(loc.x + Math.cos(dir) * radius), (int)(loc.y + Math.sin(dir) * radius));
    
        float d = frontSensor();
        
        g.setColor(Color.red);
        g.drawString("d: " + d + "pixels", 10, 150);
        g.drawLine((int)getLoc().x, (int)getLoc().y, (int)(getLoc().x + Math.cos(getDir()) * d), (int)(getLoc().y + Math.sin(getDir()) * d));
    
    }
    
    public void update()
    {
        loc.x += Math.cos(dir) * speed;
        loc.y += Math.sin(dir) * speed;
    }
    
    public void setSpeed(float s)
    {
        this.speed = s;
    }
    
    public void changeDir(float d)
    {
        this.dir += d;
    }
    
    public PointF getLoc()
    {
        return this.loc;
    }
    public float getDir()
    {
        return this.dir;
    }
    
    private boolean insideRect(double x, double y, Rectangle r)
    {
        return r.pt1.x < x - 4 && x + 4 < r.pt2.x &&
               r.pt1.y < y - 4 && y + 4 < r.pt2.y;
    }
    
    private Point calcy(Rectangle r, double y)
    {
        double m=0,b=0,x=0;
        
            m = Math.tan(dir);
            b = loc.y - (m*loc.x);
            if(Double.isFinite(b) && Double.isFinite(b))
                x = (y - b) / m;
            if (Math.abs(Math.cos(dir)) < .1 || Double.isInfinite(y))
                x = loc.x;
            
        boolean in = false;
        if (r.pt1.x < x && x < r.pt2.x)
        {
            for (Rectangle r2 : room.rects)
            {
                if (r != r2 && insideRect(x,y,r2))
                {
                    in = true;
                }
            }
        }
        if (!in)
            return new Point((int)x,(int)y);
        return null;
    }
    
    private Point calcx(Rectangle r, double x)
    {
        double m=0,b=0,y=0;
        
        {
            m = 1/Math.tan(dir);
            b = loc.x - (m*loc.y);
            if(Double.isFinite(b) && Double.isFinite(b))
                y = (x - b) / m;
            if (Math.abs(Math.sin(dir)) < .1 || Double.isInfinite(y))
                y = loc.y;
        }
        boolean in = false;
        if (r.pt1.y < y && y < r.pt2.y)
        {
            for (Rectangle r2 : room.rects)
            {
                if (r != r2 && insideRect(x,y,r2))
                {
                    in = true;
                }
            }
        }
        if (!in)
            return new Point((int)x,(int)y);
        return null;
    }
    
    public float frontSensor()
    {
        ArrayList<Point> pts = new ArrayList<Point>();
        Point p;
        for (Rectangle r : room.rects)
        {
            if (Math.sin(dir) < 0 && r.pt1.y < loc.y)
            {
                    p = calcy(r, r.pt1.y);
                    if (p != null)
                        pts.add(p);
            }
            else if (Math.sin(dir) > 0 && r.pt2.y > loc.y)
            {
                p = calcy(r, r.pt2.y);
                if (p != null)
                    pts.add(p);
            }
            if (Math.cos(dir) > 0 && r.pt2.x > loc.x)
            {
                p = calcx(r, r.pt2.x);
                if (p != null)
                    pts.add(p);
            }
            else if (Math.cos(dir) < 0 && r.pt1.x < loc.x)
            {
                p = calcx(r, r.pt1.x);
                if (p != null)
                    pts.add(p);
            }
        }
        float min = Float.MAX_VALUE;
        float d;
        for (Point pt : pts)
        {
            d = room.Distance(pt, new Point(loc));
            min = Float.min(d, min);
        }
        return min;
    }
}
