
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//hacer un mutex con la clase hecha por nosotros y otra con la librería en total 5 tanques
//graficar condiciones de competencia, variable de cerradura, alternancia estricta, mutex con concurrent

public class ProductorSemaforo extends Thread {

    private Semaphore s;
    private LiFoTanke tanke[];
    private boolean isRunning;
    private int x, y;
    private int executionCount;
    private boolean resetRequested;
    private Color color;

    public ProductorSemaforo(Semaphore semaforo, LiFoTanke tanke[], boolean isRunning, int x, int y, Color color) {
        this.tanke = tanke;
        this.s = semaforo;
        this.isRunning = isRunning;
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
            
            try {
                s.acquire();
                if (tanke[0].size() < 20) {
                    tanke[0].pushAgua(new Agua(x, y - ((y / 20) - 2) * tanke[0].size(), 100, (y / 20) - 2, color));
                    executionCount++;
                    //dibujar.repaint();
                }
            } catch (InterruptedException e) {} 
            finally {
                s.release();
            }

            try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            } catch (InterruptedException ex) {}

            
            /*
            if (tanke.size() < 20) {
                try {
                    s.acquire();
                    tanke.pushAgua(new Agua(new Rectangle2D.Double(x, y - ((y / 20) - 2) * tanke.size(), 100, (y / 20) - 2), color));
                    executionCount++;
                    dibujar.atualizar(n);
                    //System.out.println("producir");
                    //Thread.sleep(25);
                    //s.release();
                    Thread.sleep((int) (Math.random() * 100) + 1000);
                } catch (Exception ex) {} finally {
                    s.release();
                }
            }
            */
            /*try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            }catch (InterruptedException ex) {}*/
            
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
