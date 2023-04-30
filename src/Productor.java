
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class Productor extends Thread {

    private final LiFoTanke tanke;
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;
    private Color color;
    
    private final int x, y;

    Productor(LiFoTanke tanke, boolean isRunning, int x, int y, Color color) {
        this.tanke = tanke;
        this.isRunning = isRunning;
        this.x = x; //x+(sep*0)
        this.y = y; //h+40
        this.color = color;
    }

    //Paso 3:
    @Override
    public void run() {
        while (true) {
            while (!isRunning) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (tanke.size() < 20) {
                tanke.pushAgua(new Agua(new Rectangle2D.Double(x, y - ((y/20)-2) * tanke.size(), 100, (y/20)-2),color));
            
                executionCount++;
            }
            if (resetRequested) {
                executionCount = 0;
                resetRequested = false;
            }
        }
    }

    public int getExecutionCount() {
        int aux = executionCount;
        resetRequested = true;
        return aux;
    }
    
    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}