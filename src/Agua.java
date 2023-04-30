import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Agua {
    private Rectangle2D agua;
    private Color color;
 
    public Rectangle2D getAgua() {
        return agua;
    }

    public Color getColor() {
        return color;
    }
    

    Agua(Rectangle2D agua, Color color) {
        this.agua = agua;
        this.color = color;
    }
    
}
