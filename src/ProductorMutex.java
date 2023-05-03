
import java.awt.Color;
import java.util.concurrent.locks.*;
import mpi.MPI;
//hacer un mutex con la clase hecha por nosotros y otra con la librería en total 5 tanques
//graficar condiciones de competencia, variable de cerradura, alternancia estricta, mutex con concurrent

public class ProductorMutex extends Thread {

    private Lock mutex;
    private LiFoTanke tanke[];
    private boolean isRunning;
    private int x, y;
    private int executionCount;
    private boolean resetRequested;
    private Color color;

    public ProductorMutex(Lock mutex, LiFoTanke tanke[], boolean isRunning, int x, int y, Color color) {
        this.tanke = tanke;
        this.isRunning = isRunning;
        this.x = x;
        this.y = y;
        this.mutex = mutex;
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
            /*if (tanke[0].size() < 20) {
                mutex.lock();
                tanke[0].pushAgua(new Agua(x, y - ((y / 20) - 2) * tanke[0].size(), 100, (y / 20) - 2, color));
                executionCount++;
                MPI.COMM_WORLD.Isend(tanke, 0, 1, MPI.OBJECT, 0, 0);
                mutex.unlock();
            }
            try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            } catch (InterruptedException ex) {}
            */
            
            
            try {
                mutex.lock();
                if (tanke[0].size() < 20) {
                    tanke[0].pushAgua(new Agua(x, y - ((y / 20) - 2) * tanke[0].size(), 100, (y / 20) - 2, color));
                    executionCount++;
                    MPI.COMM_WORLD.Isend(tanke, 0, 1, MPI.OBJECT, 0, 0);
                    //Thread.sleep((int) (Math.random() * 100) + 1000);
                }
                Thread.sleep((int) (Math.random() * 100) + 1000);
            } catch (InterruptedException ex) {
            } finally {
                mutex.unlock();//mutex abre el candado para que sea usado por mutex
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
