package org.ajwerner.voronoi;

/**
 * Created by ajwerner on 12/28/13.
 */
public class BreakPoint
{
    private final Voronoi v;
    public final Point s1, s2;
    public VoronoiEdge e;
    public boolean isEdgeLeft;
    public final Point edgeBegin;
    
    private double cacheSweepLoc;
    private Point cachePoint;
    
    public BreakPoint(Point left, Point right, VoronoiEdge e, boolean isEdgeLeft, Voronoi v)
    {
        this.v = v;
        this.s1 = left;
        this.s2 = right;
        this.e = e;
        this.isEdgeLeft = isEdgeLeft;
        this.edgeBegin = this.getPoint();
    }
    
    private static double sq(double d)
    {
        return d * d;
    }
    
    public void finish(Point vert)
    {
        if (isEdgeLeft)
        {
            this.e.p1 = vert;
        }
        else
        {
            this.e.p2 = vert;
        }
    }
    
    public void finish(double MIN_Y, double MAX_Y)
    {
        Point p;
        boolean isBelow = (s1.y > s2.y) ^ (s1.x > s2.x) ^ isEdgeLeft; //I am so proud of my success!!!
        double sweepLoc = isBelow ? MIN_Y : MAX_Y;
        p = getPointInner(sweepLoc);
        
        if (isEdgeLeft)
        {
            this.e.p1 = p;
        }
        else
        {
            this.e.p2 = p;
        }
        
    }
    
    public Point getPoint()
    {
        double sweepLoc = v.getSweepLoc();
        if (sweepLoc == cacheSweepLoc)
        {
            return cachePoint;
        }
        cacheSweepLoc = sweepLoc;
        cachePoint = getPointInner(sweepLoc);
        return cachePoint;
    }
    
    public String toString()
    {
        return String.format("%s \ts1: %s\ts2: %s", this.getPoint(), this.s1, this.s2);
    }
    
    public VoronoiEdge getEdge()
    {
        return this.e;
    }
    
    
    public Point getPointInner(double sweepLoc)
    {
        double x, y;
        // Handle the vertical line case
        if (s1.y == s2.y)
        {
            x = (s1.x + s2.x) / 2; // x coordinate is between the two points
            // comes from parabola focus-directrix definition:
            y = (sq(x - s1.x) + sq(s1.y) - sq(sweepLoc)) / (2 * (s1.y - sweepLoc));
        }
        else
        {
            // This method works by intersecting the line of the edge with the parabola of the higher point
            // I'm not sure why I chose the higher point, either should work
            double px = (s1.y > s2.y) ? s1.x : s2.x;
            double py = (s1.y > s2.y) ? s1.y : s2.y;
            double m = e.slope;
            double b = e.intercept;
            
            double d = 2 * (py - sweepLoc);
            
            // Straight up quadratic formula
            double A = 1;
            double B = -2 * px - d * m;
            double C = sq(px) + sq(py) - sq(sweepLoc) - d * b;
            int sign = (s1.y > s2.y) ? -1 : 1;
            double det = sq(B) - 4 * A * C;
            // When rounding leads to a very very small negative determinant, fix it
            if (det <= 0)
            {
                x = -B / (2 * A);
            }
            else
            {
                x = (-B + sign * Math.sqrt(det)) / (2 * A);
            }
            y = m * x + b;
        }
        return new Point(x, y);
    }
}
