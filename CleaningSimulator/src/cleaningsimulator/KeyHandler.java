/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleaningsimulator;

import java.awt.event.*;

/**
 *
 * @author kram1
 */
public class KeyHandler implements KeyListener
{
    WindowFrame frame;
    public KeyHandler(WindowFrame frame)
    {
        this.frame = frame;
    }
    
    public void keyTyped(KeyEvent e)
    {
    }
    
    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                frame.upKey = true;
                break;
            case KeyEvent.VK_DOWN:
                frame.downKey = true;
                break;
            case KeyEvent.VK_RIGHT:
                frame.rightKey = true;
                break;
            case KeyEvent.VK_LEFT:
                frame.leftKey = true;
                break;
            case KeyEvent.VK_H:
                frame.hKey = true;
                break;
        }
    }
    
    public void keyReleased(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                frame.upKey = false;
                break;
            case KeyEvent.VK_DOWN:
                frame.downKey = false;
                break;
            case KeyEvent.VK_RIGHT:
                frame.rightKey = false;
                break;
            case KeyEvent.VK_LEFT:
                frame.leftKey = false;
                break;
            case KeyEvent.VK_H:
                frame.hKey = false;
                break;
        }
    }
}
