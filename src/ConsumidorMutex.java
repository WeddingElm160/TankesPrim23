
import java.util.concurrent.locks.*;
import mpi.MPI;

public class ConsumidorMutex extends Thread {

    private Lock mutex;
    private LiFoTanke tanke[];
    private int n;
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;

    public ConsumidorMutex(Lock mutex, LiFoTanke tanke[], boolean isRunning, int n) {
        this.tanke = tanke;
        this.n = n;
        this.mutex = mutex;
        this.isRunning = isRunning;
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
            if (!tanke[0].isEmpty()) {
                mutex.lock();
                tanke[0].popAgua();
                executionCount++;
                //dibujar.repaint();
                //MPI.COMM_WORLD.Send(tanke, 0, 1, MPI.OBJECT, 0, 0);
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
