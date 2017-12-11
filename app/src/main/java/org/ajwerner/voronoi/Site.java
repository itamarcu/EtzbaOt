package org.ajwerner.voronoi;

public class Site extends Point
{
    public final int ID;
    
    private static int lastIDgiven = 17;
    
    public Site(int x, int y)
    {
        super(x, y);
        
        ID = lastIDgiven;
        lastIDgiven++;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Site))
        {
            return false;
        }
        return ((Site) (obj)).ID == this.ID;
    }
    
    
    public double sqrDistanceTo(ScreenPoint that) {
        return (this.x - that.x)*(this.x - that.x) + (this.y - that.y)*(this.y - that.y);
    }
}