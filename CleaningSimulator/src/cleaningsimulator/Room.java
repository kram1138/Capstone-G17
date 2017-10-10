/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author kram1
 */
public class Room extends SimObject
{
    ArrayList<RoomNode> nodes;
    Robot robot;
    public Room(Robot r, WindowFrame frame)
    {
        nodes = new ArrayList<RoomNode>();
        robot = r;
        for (int i = 0; i < 40; i++)
        {
            for (int j = 0; j < 40; j++)
                nodes.add(new RoomNode(i * 20,j * 20,frame));
        }
        frame.toDraw.add(this);
    }
    
    public void update()
    {
        for (int i = nodes.size() - 1; i >= 0; i--)
        {
            if ((int)Distance(nodes.get(i).x, nodes.get(i).y, (int)robot.x, (int)robot.y) < (int)robot.radius)
            {
                nodes.remove(i);
            }
        }
    }
    
    public float Distance(int x1, int y1, int x2, int y2)
    {
        float g = (float)Math.sqrt(Math.pow(x2 - x1,2) + Math.pow(y2 - y1,2));
        return g;
    }
    
    public void paint(Graphics g)
    {
        for (RoomNode node : nodes)
        {
            node.paint(g);
        }
    }
}
