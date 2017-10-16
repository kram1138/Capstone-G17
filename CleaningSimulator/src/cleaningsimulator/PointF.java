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
public class PointF
{
    public float x;
    public float y;
    
    public PointF(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    public PointF(Point p)
    {
        this.x = p.x;
        this.y = p.y;
    }
}
