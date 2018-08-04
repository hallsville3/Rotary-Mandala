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

/**
 * A Cartesian point with polar functionality.
 *
 * @author chasehanson
 */
public class Point {

    /**
     * The x coordinate of the <code>Point</code>.
     */
    public double x,
            /**
             * The y coordinate of the <code>Point</code>.
             */
            y;

    /**
     * Constructs a <code>Point</code> at the origin.
     */
    public Point() {
        this(0, 0);
    }

    /**
     * Constructs a <code>Point</code> at the given coordinate (x, y)
     *
     * @param x The x coordinate of the <code>Point</code>.
     * @param y The y coordinate of the <code>Point</code>.
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a <code>String</code> representation of <code>this</code> in the
     * form (x, y).
     * 
     * @return A <code>String</code> of the form (x, y).
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Returns a new <code>Point</code>, rotated by a given angle theta.
     *
     * @param theta The angle of rotation.
     * @return The newly rotated <code>Point</code>.
     */
    public Point rotate(double theta) {
        double[] polar = asPolar();
        return fromPolar(polar[0], polar[1] + theta);
    }

    /**
     * Returns an array, representing this <code>Point</code> in polar
     * coordinates (R, theta).
     *
     * @return polar representation of this <code>Point</code> as a
     * <code>double[]</code>.
     */
    public double[] asPolar() {
        double theta;
        try {
            theta = Math.atan(Math.abs(y / x));
        } catch (Exception e) {
            theta = Math.PI / 2;
        }
        if (x > 0) {
            if (y >= 0) {
                //theta is correct
            } else {
                theta += 3 * Math.PI / 2;
            }
        } else {
            if (y >= 0) {
                theta = Math.PI - theta;
            } else {
                theta += Math.PI;
            }
        }

        return new double[]{Math.sqrt(x * x + y * y), theta};
    }

    /**
     * Returns a new <code>Point</code> constructed from polar coordinates (R,
     * theta).
     *
     * @param R The radius R of the polar point.
     * @param theta The angle theta of the polar point.
     * @return A new <code>Point</code> constructed from the polar coordinate.
     */
    public static Point fromPolar(double R, double theta) {
        return new Point(Math.cos(theta) * R, Math.sin(theta) * R);
    }

    /**
     * Returns a <code>Point[]</code> of <code>n</code> linearly interpolated
     * points between <code>this</code> and <code>p</code>.
     *
     * @param p The <code>Point</code> to be interpolated between
     * @param n The number of <code>Points</code> between <code>this</code> and
     * <code>p</code>
     * @return A Point[] of the interpolated Points
     */
    public Point[] interpolate(Point p, int n) {
        Point[] points = new Point[n];

        double minX = Math.min(p.x, x);
        double maxX = Math.max(p.x, x);
        double xDifference = maxX - minX;

        double minY = Math.min(p.y, y);
        double maxY = Math.max(p.y, y);
        double yDifference = maxY - minY;

        double startingY = (p.x == minX ? p.y : y);
        for (int i = 0; i < n; i++) {
            points[i] = new Point(minX + xDifference / n * i, startingY + i * ((startingY == minY) ? yDifference / n : -yDifference / n));
        }
        return points;
    }

}
