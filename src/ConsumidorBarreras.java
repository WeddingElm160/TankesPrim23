import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import mpi.MPI;

public class ConsumidorBarreras extends Thread {

    private final LiFoTanke tanke[];
    private int n;
    private boolean isRunning;
    private CyclicBarrier barrera;
    private int executionCount;
    private boolean resetRequested;

    /*private int executionCount;
    private boolean resetRequested;*/
    public ConsumidorBarreras(CyclicBarrier barrera, LiFoTanke tanke[], int n) {
        this.barrera = barrera;
        this.tanke = tanke;
        this.n = n;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (true) {
            //semaforo.release();

            while (!isRunning) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            if (!tanke[0].isEmpty()) {
                    tanke[0].popAgua();
                    executionCount++;
                    MPI.COMM_WORLD.Isend(tanke, 0, 1, MPI.OBJECT, 0, 0);
                try {
                    barrera.await();
                } catch (InterruptedException ex) {} 
                catch (BrokenBarrierException ex) {}
                catch (Exception ex) {}
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
