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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Represents the screen into which the Mandala is drawn - allows for graphics.
 * Also, handles mouse clicking behavior
 *
 * @author chasehanson
 */
public class Frame extends JPanel {

    /**
     * The stored <code>Mandala</code> object
     */
    public Mandala m;

    /**
     * <code>true</code> if the mouse is currently pressed, otherwise
     * <code>false</code>
     */
    public boolean mousePressed;

    /**
     * Constructs a new <code>Frame</code> based on a default
     * <code>JPanel</code>
     */
    public Frame() {
        super();
    }

    /**
     * Triggered upon clicking the mouse
     */
    public void pressed() {
        mousePressed = true;
    }

    /**
     * Triggered upon releasing the mouse. Also, causes the <code>Mandala</code>
     * to reduce drawing latency and clear stored points
     */
    public void released() {
        mousePressed = false;
        m.offloadPoints();
        m.reducePoints(m.points.size());
    }

    /**
     * Stores a new <code>Mandala</code> in the Frame
     *
     * @param m The new <code>Mandala</code> object
     */
    public void add(Mandala m) {
        this.m = m;
    }

    @Override
    public void paintComponent(Graphics G) {
        //Draws each frame
        super.paintComponent(G);
        G.setColor(Color.white);
        G.fillRect(0, 0, WIDTH, HEIGHT);

        Dimension d = getSize();

        if (m != null) {
            m.draw(G, d);

        }
    }
}
