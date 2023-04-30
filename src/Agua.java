import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Agua extends Rectangle2D.Double{
    private Color color;

    public Color getColor() {
        return color;
    }
    
    Agua(double x, double y, double w, double h, Color color) {
        super(x,y,w,h);
        this.color = color;
    }
    
}
