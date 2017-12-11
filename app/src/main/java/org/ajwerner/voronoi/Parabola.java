package org.ajwerner.voronoi;

/**
 * Created by ajwerner on 12/29/13.
 */
public class Parabola {
    private final double a, b, c;

    public Parabola(Point focus, double directrixY) {
        this.a = focus.x;
        this.b = focus.y;
        this.c = directrixY;
    }
}
