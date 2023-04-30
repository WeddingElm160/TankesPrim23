
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ProductorBarreras extends Thread {

    private final LiFoTanke tanke[];
    private final int x, y;
    private Color color;
    private boolean isRunning;
    private CyclicBarrier barrera;
    private int executionCount;
    private boolean resetRequested;

    public ProductorBarreras(CyclicBarrier barrera, LiFoTanke tanke[], int x, int y, Color color) {
        this.barrera = barrera;
        this.tanke = tanke;
        this.x = x;
        this.y = y;
        this.color = color;
        this.isRunning = true;
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
           
            if (tanke[0].size() < 20) {
                tanke[0].pushAgua(new Agua(x, y - ((y / 20) - 2) * tanke[0].size(), 100, (y / 20) - 2, color));
                executionCount++;
                //dibujar.repaint();
                try {
                    barrera.await();
                } catch (InterruptedException ex) {
                } catch (BrokenBarrierException ex) {
                }catch (Exception ex) {}
            }
            try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            } catch (InterruptedException ex) {}
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
