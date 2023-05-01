
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import mpi.MPI;

public class ConsumidorVC extends Thread {

    private final LiFoTanke tanke[];
    private int n;
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;
    private Lock mutex;
    private Condition condition;

    ConsumidorVC(LiFoTanke tanke[], boolean isRunning, Lock mutex, Condition condition, int n) {
        this.tanke = tanke;
        this.n = n;
        this.isRunning = isRunning;
        this.mutex = mutex;
        this.condition = condition;
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
            mutex.lock();
            try {
                while (tanke[0].size() == 0) {
                    condition.await();
                }
                tanke[0].popAgua();
                executionCount++;
                MPI.COMM_WORLD.Isend(tanke, 0, 1, MPI.OBJECT, 0, 0);
                condition.signal();
            } catch (Exception e) {} 
            finally {
                mutex.unlock();
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