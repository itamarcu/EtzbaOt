package org.ajwerner.voronoi;

/**
 * Created by ajwerner on 12/28/13.
 * Edited by itamarcu.
 */
public class VoronoiEdge
{
    /**
     * One of these must always be a starter site!
     */
    public final Point site1, site2;
    public final double slope, intercept; // parameters for line that the edge lies on
    public final boolean isVertical;
    public Point p1, p2;
    
    public VoronoiEdge(Point site1, Point site2)
    {
        this.site1 = site1;
        this.site2 = site2;
        isVertical = site1.y == site2.y;
        if (isVertical)
            slope = intercept = 0;
        else
        {
            slope = -1.0 / ((site1.y - site2.y) / (site1.x - site2.x));
            Point midpoint = Point.midpoint(site1, site2);
            intercept = midpoint.y - slope * midpoint.x;
        }
    }
    
    public VoronoiEdge(Point site)
    {
        this.site1 = site;
        this.site2 = null;
        isVertical = false;
        slope = 0;
        intercept = 0;
    }
    
    public Point intersection(VoronoiEdge that)
    {
        if (this.slope == that.slope && this.intercept != that.intercept)
            return null; // no intersection
        double x, y;
        if (this.isVertical)
        {
            x = (this.site1.x + this.site2.x) / 2;
            y = that.slope * x + that.intercept;
        }
        else if (that.isVertical)
        {
            x = (that.site1.x + that.site2.x) / 2;
            y = this.slope * x + this.intercept;
        }
        else
        {
            x = (that.intercept - this.intercept) / (this.slope - that.slope);
            y = slope * x + intercept;
        }
        return new Point(x, y);
    }
    
    @Override
    public String toString()
    {
        return "VoronoiEdge{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", site1=" + site1 +
                ", site2=" + site2 +
                ", slope=" + slope +
                ", intercept=" + intercept +
                ", isVertical=" + isVertical +
                '}';
    }
}
