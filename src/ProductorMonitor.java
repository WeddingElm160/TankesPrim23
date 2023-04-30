
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class ProductorMonitor extends Thread {

    private final LiFoTanke tanke[];
    private boolean isRunning;
    private final int x, y;
    private final Color color;
    private int executionCount;
    private boolean resetRequested;

    public ProductorMonitor(LiFoTanke tanke[], int x, int y, Color color) {
        this.tanke = tanke;
        this.isRunning = true;
        this.x = x;
        this.y = y;
        this.color = color;
    }

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
            synchronized (tanke[0]) {
                while (tanke[0].size() >= 20) {
                    try {
                        tanke.wait();
                    } catch (InterruptedException ex) {
                    }
                }
                tanke[0].pushAgua(new Agua(new Rectangle2D.Double(x, y - ((y / 20) - 2) * tanke[0].size(), 100, (y / 20) - 2), color));
                executionCount++;
                //dibujar.repaint();
                tanke.notifyAll();
            }

            try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            } catch (InterruptedException ex) {
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