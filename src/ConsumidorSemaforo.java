
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//acer un mutex con la clase hecha por nosotros y otra con la librería en total 5 tanques
//graficar condiciones de competencia, variable de cerradura, alternancia estricta, mutex con concurrent

public class ConsumidorSemaforo extends Thread {

    private Semaphore s;
    private LiFoTanke tanke[];
    private int n;
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;

    public ConsumidorSemaforo(Semaphore semaforo, LiFoTanke tanke[], boolean isRunning, int n) {
        this.tanke = tanke;
        this.n = n;
        this.s = semaforo;
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
            
            try {
                s.acquire();
                if (tanke[0].size() > 0) {
                    tanke[0].popAgua();
                    executionCount++;
                    //dibujar.repaint();
                }
            } catch (Exception e) {} 
            finally {
                s.release();
            }
            
            try {
                Thread.sleep((int) (Math.random() * 100) + 1000);
            }catch (InterruptedException ex) {}
            
            
            
            /*if (!tanke.isEmpty()) {
                try {
                    s.acquire();
                    tanke.popAgua();
                    executionCount++;
                    dibujar.atualizar(n);
                    //System.out.println("consumir");
                    //Thread.sleep(25);
                    //s.release();
                    //
                    Thread.sleep((int) (Math.random() * 100) + 1000);
                } catch (Exception ex) {} 
                finally {
                    s.release();
                }
            }*/
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
