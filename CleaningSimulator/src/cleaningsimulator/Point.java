/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

/**
 *
 * @author kram1
 */
public class Point
{
    public int x;
    public int y;
    
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public Point(PointF p)
    {
        this.x = (int)p.x;
        this.y = (int)p.y;
    }
}
