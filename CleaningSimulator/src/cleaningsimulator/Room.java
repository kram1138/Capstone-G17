/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author kram1
 */
public class Room extends SimObject
{
    int eaten;
    int total;
    ArrayList<RoomNode> nodes;
    ArrayList<Rectangle> rects;
    public Room(WindowFrame frame, int sizex, int sizey, Random rand)
    {
        eaten = 0;
        nodes = new ArrayList<RoomNode>();
        rects = new ArrayList<Rectangle>();
        for (int i = 0; i < 4; i++)
        {
            int width = (int)(sizex * .8 * rand.nextDouble());
            int height = (int)(sizey * .8 * rand.nextDouble());
            int x1 = (int)(sizex*.1 + (sizex * .8 - width) * rand.nextDouble());
            int y1 = (int)(sizey*.1 + (sizey * .8 - height) * rand.nextDouble());
            int x2 = x1 + width;
            int y2 = y1 + height;
            Rectangle newRect = new Rectangle(new Point(x1,y1), new Point(x2,y2), Color.white);
            rects.add(newRect);
        }
        for (int i = 0; i < sizex/20; i++)
        {
            for (int j = 0; j < sizey/20; j++)
            {
                int x = i * 20;
                int y = j * 20;
                boolean toPlace = false;
                for (Rectangle rect : rects)
                {
                    if ((x >= rect.pt1.x && x <= rect.pt2.x) && (y >= rect.pt1.y && y <= rect.pt2.y))
                        toPlace = true;
                }
                if (toPlace)
                    nodes.add(new RoomNode(x,y,frame));
            }
        }
        total = nodes.size();
        frame.toDraw.add(this);
    }
    
    public void update(Robot robot)
    {
        for (int i = nodes.size() - 1; i >= 0; i--)
        {
            if ((int)Distance(nodes.get(i).loc, new Point(robot.getLoc())) < (int)robot.radius)
            {
                nodes.remove(i);
                eaten++;
            }
        }
    }
    
    public float Distance(Point p1, Point p2)
    {
        float g = (float)Math.sqrt(Math.pow(p2.x - p1.x,2) + Math.pow(p2.y - p1.y,2));
        return g;
    }
    
    public void paint(Graphics g)
    {
        for (Rectangle rect : rects)
        {
            rect.paint(g);
        }
        for (RoomNode node : nodes)
        {
            node.paint(g);
        }
        g.setColor(Color.white);
        g.drawString("Percentage covered: " + ((float)eaten/total) * 100 + "%", 10, 100);
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;
        g.drawString("x: " + x + " y: " + y, 10, 180);
    }
}
