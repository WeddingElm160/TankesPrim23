
import java.util.Stack;


public class Consumidor extends Thread{
    private final LiFoTanke tanke;
    private boolean isRunning;
    private int executionCount;
    private boolean resetRequested;
    
    Consumidor(LiFoTanke tanke, boolean isRunning){
        this. tanke = tanke;
        this.isRunning = isRunning;
    }
    
    @Override
    public void run(){
        while(true){
            while (!isRunning) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if(tanke.size()>0){
                tanke.popAgua();
                executionCount++;
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