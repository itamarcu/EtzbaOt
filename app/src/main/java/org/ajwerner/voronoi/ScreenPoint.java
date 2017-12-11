package org.ajwerner.voronoi;

/**
 * Created by itamarcu on 2017-12-11
 */
public class ScreenPoint
{
    public final int x, y;
    public final VoronoiEdge edge;
    
    public ScreenPoint(Point point, VoronoiEdge edge)
    {
        this.x = (int) point.x;
        this.y = (int) point.y;
        this.edge = edge;
    }
    
    public ScreenPoint(double x, double y, VoronoiEdge edge)
    {
        this.x = (int) x;
        this.y = (int) y;
        this.edge = edge;
    }
    
    public ScreenPoint(Point point)
    {
        this.x = (int) point.x;
        this.y = (int) point.y;
        this.edge = null;
    }
    
    public ScreenPoint(double x, double y)
    {
        this.x = (int) x;
        this.y = (int) y;
        this.edge = null;
    }
    
    @Override
    /**
     * only x and y
     */
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        
        ScreenPoint that = (ScreenPoint) o;
        
        if (x != that.x)
            return false;
        if (y != that.y)
            return false;
        return true;
    }
    
    @Override
    /**
     * only x and y
     */
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        return result;
    }
    
    public double sqrDistanceTo(ScreenPoint that)
    {
        return (this.x - that.x) * (this.x - that.x) + (this.y - that.y) * (this.y - that.y);
    }
    
    public String toString()
    {
        return String.format("(%d, %d)", this.x, this.y) + " edge: " + (edge == null ? "null" : edge);
    }
}
