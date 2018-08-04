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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;

/**
 * Represents a set of Points with equidistant copies rotated around the origin.
 * Additionally, contains a second set of Points, rotated with a small offset.
 * The number of copies is determined by a preset number of segments. This
 * creates a mandala where lines appear to meet at the edges of the segments.
 *
 * @author chasehanson
 */
public class Mandala {

    /**
     * A <code>CopyOnWriteArrayList</code> is used to hold the Points. This is
     * done to avoid concurrent modification errors
     */
    public CopyOnWriteArrayList<Point> points;

    /**
     * The number of segments used when rotating new Points around the origin.
     */
    public int segments;

    /**
     * The background image used to offload points.
     */
    public BufferedImage im;

    /**
     * The width of the <code>Mandala</code> in pixels
     */
    public int width,
            /**
             * The height of the <code>Mandala</code> in pixels.
             */
            height;

    /**
     * The border pixel width to be used on the <code>BufferedImage</code>.
     */
    public int border;

    /**
     * Constructs a new <code>Mandala</code> of given <code>width</code> and
     * <code>height</code> with 8 segments.
     *
     * @param width The width of the <code>Mandala</code> in pixels
     * @param height The height of the <code>Mandala</code> in pixels
     */
    public Mandala(int width, int height) {
        segments = 8;
        setup(width, height);
    }

    /**
     * Constructs a new <code>Mandala</code> of given width, height, and number
     * of segments.
     *
     * @param s The number of segments to use
     * @param width The width of the <code>Mandala</code> in pixels
     * @param height The height of the <code>Mandala</code> in pixels
     */
    public Mandala(int s, int width, int height) {
        segments = s;
        setup(width, height);
    }

    /**
     * Sets the border size, width, and height of the <code>Mandala</code>.
     * Also, initializes the <code>Point CopyOnWriteArrayList</code> and makes
     * the <code>BufferedImage</code> all white
     *
     * @param w The width of the <code>Mandala</code> in pixels
     * @param h The height of the <code>Mandala</code> in pixels
     */
    public void setup(int w, int h) {
        border = 4;
        width = w;
        height = h;
        points = new CopyOnWriteArrayList<>();
        im = new BufferedImage(w + border * 2, h + border * 2, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < w + border * 2; i++) {
            for (int j = 0; j < h + border * 2; j++) {
                im.setRGB(i, j, Color.white.getRGB());
            }
        }
    }

    /**
     * Reruns setup in order to reinitialize the <code>Mandala</code> to its
     * initial state.
     */
    public void clear() {
        setup(width, height);
    }

    /**
     * Adds a new point to the <code>Mandala</code>, alongside all its
     * rotations.
     *
     * @param x The x coordinate of the master <code>Point</code>
     * @param y The y coordinate of the master <code>Point</code>
     * @param radius The maximum allowed radius of the <code>Mandala</code>
     */
    public void addPoint(double x, double y, double radius) {
        Point original = new Point(x, y);

        double segmentAngle = 2 * Math.PI / segments;
        double originalAngle = original.asPolar()[1];
        double ratio = originalAngle / segmentAngle;

        int segment = (int) (ratio + .5); //Segment which master point lies in

        double closestSegmentAngle = segment * segmentAngle;
        double difference = closestSegmentAngle - originalAngle;

        for (int i = 0; i < segments; i++) {
            checkPointAndAdd(original.rotate(difference * 2 + segmentAngle * i), radius); //Nearby Point
            checkPointAndAdd(original.rotate(segmentAngle * i), radius); //Distant Point
        }

    }

    private void checkPointAndAdd(Point p, double radius) {
        //Verifies that a given point is within the required radius
        //If not, reduces radius to the maximum

        if (p.asPolar()[0] <= radius) {
            points.add(p);
        } else {
            double[] polar = p.asPolar();
            points.add(Point.fromPolar(radius, polar[1]));
        }
    }

    /**
     * Draws the <code>Mandala</code> in two steps. First, the stored
     * <code>BufferedImage</code> is drawn. Second, any points in the
     * <code>ArrayList</code> are drawn individually. If there are too many
     * points in the <code>ArrayList</code>, they are offloaded to the
     * <code>BufferedImage</code> after the frame is drawn.
     *
     * @param G The <code>Graphics</code> instance to draw into
     * @param d The <code>Dimension</code> of the screen
     */
    public void draw(Graphics G, Dimension d) {
        G.setColor(Color.black);
        G.drawImage(im, -border, -border, null);
        boolean reduced = false;

        //Platform dependent, raise or lower to adjust when points are offloaded
        int cap = 3000;

        if (points.size() > cap) {
            offloadPoints();
            reduced = true;
        }

        for (Point p : points) {
            G.fillRect(d.width / 2 + (int) (.5 + p.x), d.height / 2 + (int) (.5 + p.y), 1, 1);
        }

        if (reduced) {
            reducePoints(cap);
        }
    }

    /**
     * Moves existing points into the <code>BufferedImage</code>
     */
    public void offloadPoints() {
        for (Point p : points) {
            try {
                im.setRGB(border + width / 2 + (int) (.5 + p.x), border + height / 2 + (int) (.5 + p.y), Color.black.getRGB());
            } catch (Exception e) {

            }
        }
    }

    /**
     * After offloading, reduces points in the <code>ArrayList</code> to reduce latency
     *
     * @param n
     */
    public void reducePoints(int n) {
        try {
            points = new CopyOnWriteArrayList(points.subList(n, points.size()));
        } catch (Exception e) {

        }
    }

    /**
     * Saves the <code>Mandala</code> to a bitmap of the given name
     *
     * @param name The name to save under
     * @throws IOException
     */
    public void save(String name) throws IOException {
        offloadPoints();
        reducePoints(points.size());

        File f = new File(name + ".bmp");
        ImageIO.write(im, "bmp", f);

    }

}
