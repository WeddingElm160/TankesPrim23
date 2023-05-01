
import mpi.MPI;


public class ConsumidorMonitor extends Thread {

    private final LiFoTanke tanke[];
    private int n;
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;

    public ConsumidorMonitor(LiFoTanke tanke[], boolean isRunning, int n) {
        this.tanke = tanke;
        this.n = n;
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
            synchronized (tanke[0]) {
                while (tanke[0].isEmpty()) {
                    try {
                        tanke[0].wait();
                    } catch (InterruptedException ex) {}
                }
                tanke[0].popAgua();
                executionCount++;
                MPI.COMM_WORLD.Isend(tanke, 0, 1, MPI.OBJECT, 0, 0);
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
