package ru.atom.geometry;

import java.util.Objects;

public class Bar implements Collider{
    private Point p1, p2;

    public Bar(int x1, int y1, int x2, int y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bar bar = (Bar) o;
        if (p1.equals(bar.p1) && p2.equals(bar.p2)) return true;
        if (p1.equals(bar.p2) && p2.equals(bar.p1)) return true;
        Bar sortedBar1 = new Bar(p1.x < p2.x ? p1.x : p2.x, p1.y < p2.y ? p1.y : p2.y,
                                 p1.x < p2.x ? p2.x : p1.x, p1.y < p2.y ? p2.y : p1.y);
        Bar sortedBar2 = new Bar(bar.p1.x < bar.p2.x ? bar.p1.x : bar.p2.x,
                                 bar.p1.y < bar.p2.y ? bar.p1.y : bar.p2.y,
                                 bar.p1.x < bar.p2.x ? bar.p2.x : bar.p1.x,
                                 bar.p1.y < bar.p2.y ? bar.p2.y : bar.p1.y);
        if (sortedBar1.p1.equals(sortedBar2.p1) && sortedBar1.p2.equals(sortedBar2.p2)) return true;
        return false;
    }

    @Override
    public boolean isColliding(Collider other) {
        if (other == null) return false;

        //this bar sorting ascending
        Bar sortedBar1 = new Bar(p1.x < p2.x ? p1.x : p2.x, p1.y < p2.y ? p1.y : p2.y,
                p1.x < p2.x ? p2.x : p1.x, p1.y < p2.y ? p2.y : p1.y);

        if (other instanceof Bar) {
            Bar bar = (Bar) other;

            //other bar sorting ascending
            Bar sortedBar2 = new Bar(bar.p1.x < bar.p2.x ? bar.p1.x : bar.p2.x,
                    bar.p1.y < bar.p2.y ? bar.p1.y : bar.p2.y,
                    bar.p1.x < bar.p2.x ? bar.p2.x : bar.p1.x,
                    bar.p1.y < bar.p2.y ? bar.p2.y : bar.p1.y);

            return isColliding(sortedBar1, sortedBar2);
        }
        else if (other instanceof Point) {
            Point point = (Point) other;
            return isColliding(sortedBar1, point);
        }
        else throw new UnsupportedOperationException();
    }

    //Tests if bar1 collides (intersects) bar2
    public boolean isColliding(Bar bar1, Bar bar2) {

        //create 4 corner points for each of bars under test
        Point b1p1 = new Point(bar1.p1.x, bar1.p1.y), b1p2 = new Point(bar1.p2.x, bar1.p2.y),
                b1p3 = new Point(bar1.p1.x, bar1.p2.y), b1p4 = new Point(bar1.p2.x, bar1.p1.y),
              b2p1 = new Point(bar2.p1.x, bar2.p1.y), b2p2 = new Point(bar2.p2.x, bar2.p2.y),
                b2p3 = new Point(bar2.p1.x, bar2.p2.y), b2p4 = new Point(bar2.p2.x, bar2.p1.y);

        //test if any corner point of bar2 is inside bar1
        if (isColliding(bar1, b2p1)) return true;
        if (isColliding(bar1, b2p2)) return true;
        if (isColliding(bar1, b2p3)) return true;
        if (isColliding(bar1, b2p4)) return true;

        //test if any corner point of bar1 is inside bar2
        if (isColliding(bar2, b1p1)) return true;
        if (isColliding(bar2, b1p2)) return true;
        if (isColliding(bar2, b1p3)) return true;
        if (isColliding(bar2, b1p4)) return true;

        //test if any side (two corner points) of bar2 intersects bar1
        if (isColliding(bar1, b2p1, b2p2)) return true;
        if (isColliding(bar1, b2p1, b2p3)) return true;
        if (isColliding(bar1, b2p2, b2p4)) return true;
        if (isColliding(bar1, b2p3, b2p4)) return true;

        //test if any side (two corner points) of bar1 intersects bar2
        if (isColliding(bar2, b1p1, b1p2)) return true;
        if (isColliding(bar2, b1p1, b1p3)) return true;
        if (isColliding(bar2, b1p2, b1p4)) return true;
        if (isColliding(bar2, b1p3, b1p4)) return true;

        return false;
    }

    //Tests if point inside bar
    public boolean isColliding(Bar bar, Point point) {
        return bar.p1.x <= point.x && bar.p2.x >= point.x && bar.p1.y <= point.y && bar.p2.y >= point.y;
    }

    //Tests if line between point1 & point2 collides (intersects) bar
    public boolean isColliding(Bar bar, Point point1, Point point2) {
        //equal points case
        if (point1.equals(point2))
            return bar.p1.x <= point1.x && bar.p2.x >= point1.x && bar.p1.y <= point1.y && bar.p2.y >= point1.y;

        //sorting line point coordinates ascending
        Point p1 = new Point(point1.x < point2.x ? point1.x : point2.x,
                point1.y < point2.y ? point1.y : point2.y);
        Point p2 = new Point(point1.x < point2.x ? point2.x : point1.x,
                      point1.y < point2.y ? point2.y : point1.y);

        //vertical line crosses bar test: (bar.p1.x <= (p1.x,p2.x) <= bar.p2.x) && (p1.y <= (bar.p1.y,bar.p2.y) <= p2.y)
        if ((p1.x >= bar.p1.x && p1.x <= bar.p2.x) && (p2.x >= bar.p1.x && p2.x <= bar.p2.x)
                && (p1.y <= bar.p1.y && p1.y <= bar.p2.y) && (p2.y >= bar.p1.y && p2.y >= bar.p2.y)) return true;

        //horizontal line crosses bar test: same as previous, just swap x & y
        if ((p1.y >= bar.p1.y && p1.y <= bar.p2.y) && (p2.y >= bar.p1.y && p2.y <= bar.p2.y)
                && (p1.x <= bar.p1.x && p1.x <= bar.p2.x) && (p2.x >= bar.p1.x && p2.x >= bar.p2.x)) return true;

        return false;
    }
}
