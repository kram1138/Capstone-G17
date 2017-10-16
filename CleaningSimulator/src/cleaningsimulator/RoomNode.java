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
public class RoomNode extends SimObject
{
    public Point loc;
    
    public RoomNode(int x, int y, WindowFrame frame)
    {
        loc = new Point(x, y);
    }
    
    public void paint(Graphics g)
    {
        g.setColor(Color.red);
        g.drawOval(loc.x - 3, loc.y - 3, 6, 6);
    }
}
