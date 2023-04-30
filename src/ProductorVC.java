
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ProductorVC extends Thread {

    private final LiFoTanke tanke[];
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;
    private Lock mutex;
    private Condition condition;
    private Color color;
    
    private final int x, y;

    ProductorVC(LiFoTanke tanke[], Lock mutex, Condition condition, boolean isRunning, int x, int y, Color color) {
        this.tanke = tanke;
        this.isRunning = isRunning;
        this.x = x;
        this.y = y;
        this.color = color;
        this.mutex = mutex;
        this.condition = condition;
    }

    @Override
    public void run() {
        while (true) {
            while (!isRunning) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {}
            }
            mutex.lock();
            try {
                while (tanke[0].size() == 20) {
                    condition.await();
                }
                tanke[0].pushAgua(new Agua(new Rectangle2D.Double(x, y - ((y / 20) - 2) * tanke[0].size(), 100, (y / 20) - 2), color));
                executionCount++;
                //dibujar.repaint();
                condition.signal();
            } catch (InterruptedException e) {} 
            finally {
                mutex.unlock();
            }
            try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            }catch (InterruptedException ex) {}
            
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