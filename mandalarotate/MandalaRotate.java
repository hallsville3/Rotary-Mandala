/* 
 * The MIT License
 *
 * Copyright 2018 chasehanson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mandalarotate;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * The main entry point for the MandalaRotate application.
 * @author chasehanson
 */
public class MandalaRotate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        Frame frame = new Frame();
        JFrame screen = new JFrame("");
        Point lastPoint = null;

        //X and Y dimensions of Mandala and Screen
        int xSize = 800;
        int ySize = 800;

        frame.setSize(xSize, ySize);
        screen.setSize(xSize, ySize + 23);

        screen.add(frame);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen.setVisible(true);
        frame.setVisible(true);
        
        //There are double this number of points, value MUST be even
        int segments = 8;
        
        Mandala m = new Mandala(segments, xSize, ySize);
        frame.add(m);
        
        //Pressing s saves the Mandala, pressing c clears it
        screen.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                return;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 's': //Save
                        try {
                            m.save("Mandala");
                            System.out.println("Saved");
                        } catch (IOException ex) {

                        }
                        break;
                    case 'c': //Clear
                        m.clear();
                        break;
                    default:
                        break;
                }
                
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                return;
            }
            
        });
        
        //Clicking toggles on a mousePressed flag and releasing toggles it back off
        frame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                return;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //Toggle on
                frame.pressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //Toggle off
                frame.released();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                return;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                return;
            }

        });
        
        //The frame handles Mandala drawing, but this main loop handles the adding of points 
        while (true) {
            Thread.sleep(17); //Sleep for 1/60th of a second
            if (frame.mousePressed) {
                try {
                    int x = frame.getMousePosition().x;
                    int y = frame.getMousePosition().y;
                    
                    m.addPoint(x - xSize / 2, y - ySize / 2, xSize / 2);
                    if (lastPoint != null) {
                        int amountToInterpolate = (int)(Math.max(Math.abs(lastPoint.x-x+xSize/2), Math.abs(lastPoint.y-y+ySize/2))+.5);
                        Point[] between = lastPoint.interpolate(new Point(x - xSize / 2, y - ySize / 2), amountToInterpolate);

                        for (int i = 0; i<amountToInterpolate; i++) {
                            m.addPoint(between[i].x, between[i].y, xSize/2);
                        }
                    }
                    lastPoint = new Point(x - xSize / 2, y - ySize / 2);
                    
                } catch (Exception e) {
                    //In case of concurrent modification of the ArrayList
                }
                
                
            } else {
                //Upon releasing the click of the mouse, reset the trail
                lastPoint = null;
            }
            
            //Draw the frame, draw the screen
            frame.repaint();
            screen.repaint();
        }

    }

}
