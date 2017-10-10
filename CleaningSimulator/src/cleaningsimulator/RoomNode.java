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
    public int x;
    public int y;
    
    public RoomNode(int x, int y, WindowFrame frame)
    {
        this.x = x;
        this.y = y;
    }
    
    public void paint(Graphics g)
    {
        g.setColor(Color.red);
        g.drawOval(x - 3, y - 3, 6, 6);
    }
}
