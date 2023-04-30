import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
import mpi.MPI;
import mpi.Status;

public class TankesPrim23 extends JFrame implements ActionListener{
    private DibujaTank panelTank;
    private GraficasPanel graficas;
    private  Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    static int h =(int) (Toolkit.getDefaultToolkit().getScreenSize().height-Toolkit.getDefaultToolkit().getScreenSize().height*0.2), 
            w = 100, y = 80, x =50, sep=125;
    
    private static boolean isRunning;
    private static Timer refresh;
    private static ProductorMutex productorM1, productorM2;
    private static ConsumidorMutex consumidorM1, consumidorM2;
    private static ProductorSemaforo productorS1, productorS2;
    private static ConsumidorSemaforo consumidorS1, consumidorS2;
    private static ProductorVC productorVC1, productorVC2;
    private static ConsumidorVC consumidorVC1, consumidorVC2;
    private static ProductorMonitor productorMo1, productorMo2;
    private static ConsumidorMonitor consumidorMo1, consumidorMo2;
    private static ProductorBarreras productorB1, productorB2;
    private static ConsumidorBarreras consumidorB1, consumidorB2;
    private static LiFoTanke[] tankes;
    private static Queue<Integer>[] volumenTankes;
    private int aux1;
    
    TankesPrim23(){
        aux1 = 0;
        tankes = new LiFoTanke[5];
        tankes[0] = new LiFoTanke("Mutex");
        tankes[1] = new LiFoTanke("Semaforos");
        tankes[2] = new LiFoTanke("Variables de condición");
        tankes[3] = new LiFoTanke("Monitores");
        tankes[4] = new LiFoTanke("Barreras");
        volumenTankes = new Queue[5];
        for(int i = 0; i<volumenTankes.length; i++)
            volumenTankes[i] = new LinkedList<>(Collections.nCopies(20, 0));
        isRunning = false;
        refresh = new Timer(0, this);
        refresh.start();
        setTitle("Tankes");
        setSize(1920,1080);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setUndecorated(true);
        panelTank = new DibujaTank(tankes,h, w, y, x, sep);
        panelTank.setSize(700, screen.height);
        graficas = new GraficasPanel(tankes, volumenTankes);
        graficas.setBounds(700, 0, screen.width-700, screen.height);
        System.out.println(screen.width+" - "+screen.height);
        
        add(panelTank,BorderLayout.WEST);
        add(graficas,BorderLayout.EAST);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Verifica si se presionó la tecla ESC
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);// Cierra la ventana
                }
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    changebtnState();
                }
            }
        });
    }


    public static LiFoTanke[] getTankes() {
        return tankes;
    }
    
    void changebtnState(){
        if (isRunning) {
            isRunning = true;
            if (tankes[0].getCuentaConsumidor().size() <= 20) {
                
                productorS1.start();
                consumidorS1.start();
                productorM1.start();
                consumidorM1.start();
                productorS2.start();
                consumidorS2.start();
                productorM2.start();
                consumidorM2.start();
                productorVC1.start();
                consumidorVC1.start();
                productorVC2.start();
                consumidorVC2.start();
                productorMo1.start();
                consumidorMo1.start();
                productorMo2.start();
                consumidorMo2.start();
                productorB1.start();
                consumidorB1.start();
                productorB2.start();
                consumidorB2.start();
            } else {
                graficas.reanudarGrafica();
            }
            refresh.start();
            graficas.setVisible(true);
        } else {
            isRunning = false;
            productorS1.interrupt();
            consumidorS1.interrupt();
            productorM1.interrupt();
            consumidorM1.interrupt();
            productorS2.interrupt();
            consumidorS2.interrupt();
            productorM2.interrupt();
            consumidorM2.interrupt();
            productorVC1.interrupt();
            consumidorVC1.interrupt();
            productorVC2.interrupt();
            consumidorVC2.interrupt();
            productorMo1.interrupt();
            consumidorMo1.interrupt();
            productorMo2.interrupt();
            consumidorMo2.interrupt();
            productorB1.interrupt();
            consumidorB1.interrupt();
            productorB2.interrupt();
            consumidorB2.interrupt();
            refresh.stop();
            graficas.pausarGrafica();
        }
        productorS1.setIsRunning(isRunning);
        consumidorS1.setIsRunning(isRunning);
        productorM1.setIsRunning(isRunning);
        consumidorM1.setIsRunning(isRunning);
        productorS2.setIsRunning(isRunning);
        consumidorS2.setIsRunning(isRunning);
        productorM2.setIsRunning(isRunning);
        consumidorM2.setIsRunning(isRunning);
        productorVC1.setIsRunning(isRunning);
        consumidorVC1.setIsRunning(isRunning);
        productorVC2.setIsRunning(isRunning);
        consumidorVC2.setIsRunning(isRunning);
        productorMo1.setIsRunning(isRunning);
        consumidorMo1.setIsRunning(isRunning);
        productorMo2.setIsRunning(isRunning);
        consumidorMo2.setIsRunning(isRunning);
        productorB1.setIsRunning(isRunning);
        consumidorB1.setIsRunning(isRunning);
        productorB2.setIsRunning(isRunning);
        consumidorB2.setIsRunning(isRunning);
    }
    
    public void atualizar(int n){
        panelTank.repaint();
        /*switch(n){
            case 0: tanke1.desplazar(productorM1.getExecutionCount()+productorM2.getExecutionCount(),consumidorM1.getExecutionCount()+consumidorM2.getExecutionCount()); break;
            case 1: tanke2.desplazar(productorS1.getExecutionCount()+productorS2.getExecutionCount(),consumidorS1.getExecutionCount()+consumidorS2.getExecutionCount()); break;
            case 2: tanke3.desplazar(productorVC1.getExecutionCount()+productorVC2.getExecutionCount(),consumidorVC1.getExecutionCount()+consumidorVC2.getExecutionCount()); break;
            case 3: tanke4.desplazar(productorMo1.getExecutionCount()+productorMo2.getExecutionCount(),consumidorMo1.getExecutionCount()+consumidorMo2.getExecutionCount()); break;
            case 4: tanke5.desplazar(productorB1.getExecutionCount()+productorB2.getExecutionCount(),consumidorB1.getExecutionCount()+consumidorB2.getExecutionCount()); break;
        }
        graficas.actualizarGrafica(n);*/
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //MPI.COMM_WORLD.Recv(aux1, 0, 1, MPI.INT, 1, 0);
        /*tankes[0].desplazar(productorM1.getExecutionCount()+productorM2.getExecutionCount(),consumidorM1.getExecutionCount()+consumidorM2.getExecutionCount());
        tankes[1].desplazar(productorS1.getExecutionCount()+productorS2.getExecutionCount(),consumidorS1.getExecutionCount()+consumidorS2.getExecutionCount());
        tankes[2].desplazar(productorVC1.getExecutionCount()+productorVC2.getExecutionCount(),consumidorVC1.getExecutionCount()+consumidorVC2.getExecutionCount());
        tankes[3].desplazar(productorMo1.getExecutionCount()+productorMo2.getExecutionCount(),consumidorMo1.getExecutionCount()+consumidorMo2.getExecutionCount());
        tankes[4].desplazar(productorB1.getExecutionCount()+productorB2.getExecutionCount(),consumidorB1.getExecutionCount()+consumidorB2.getExecutionCount());
        
        volumenTankes[0].add(tankes[0].size());
        volumenTankes[1].add(tankes[1].size());
        volumenTankes[2].add(tankes[2].size());
        volumenTankes[3].add(tankes[3].size());
        volumenTankes[4].add(tankes[4].size());
        
        graficas.actualizarTodasGraficas();*/
    }
    
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        System.out.println("Hola mundo - Nucleo #"+rank);
        
        LiFoTanke buffTabkes[] = new LiFoTanke[6];
        LiFoTanke tanke[] = new LiFoTanke[1];
        
        
        if(rank == 0){
            System.setProperty("sun.java2d.uiScale", "1");
            TankesPrim23 frame = new TankesPrim23();
            System.arraycopy(frame.getTankes(), 0, buffTabkes, 1, frame.getTankes().length);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        MPI.COMM_WORLD.Scatter(buffTabkes, 0, 1, MPI.OBJECT, tanke, 0, 1, MPI.OBJECT, 0);
        
        switch(rank){
            case 0:
              int dest = 0;
              while(true){
                  //MPI.COMM_WORLD.Recv(aux, 0, 1, MPI.INT, 1, 0);
                  Status s = MPI.COMM_WORLD.Recv(tanke, 0, 1, MPI.OBJECT, 1, 0);
                  
                  System.out.println(tanke[0].getName()+" -> "+s.source);
              }
            case 1:
                System.out.println(rank+" -> "+tanke[0].getName());
                Lock mutex = new ReentrantLock();
                Lock mutex2 = new ReentrantLock();
                productorM1 = new ProductorMutex(mutex, tanke, true, x + (sep * rank-1), h + 40, Color.GREEN);
                consumidorM1 = new ConsumidorMutex(mutex, tanke, true, 0);
                productorM2 = new ProductorMutex(mutex2, tanke, true,x  + (sep * rank-1), h + 40, Color.RED);
                consumidorM2 = new ConsumidorMutex(mutex2, tanke, true, 0);
                //productorM1.start(); consumidorM1.start(); productorM2.start(); consumidorM2.start();
                break;
            case 2: 
              System.out.println("dfsdf");
              MPI.COMM_WORLD.Send(tanke, 0, 1, MPI.OBJECT, 0, 0);
                /*System.out.println(rank+" -> "+tanke[0].getName());
                Semaphore semaforo = new Semaphore(1, true);
                Semaphore semaforo2 = new Semaphore(1, true);
                productorS1 = new ProductorSemaforo(semaforo, tanke, isRunning,x  + (sep * rank-1), h + 40, Color.GREEN);
                consumidorS1 = new ConsumidorSemaforo(semaforo, tanke, isRunning, 1);
                productorS2 = new ProductorSemaforo(semaforo2, tanke, isRunning,x  + (sep * rank-1), h + 40, Color.RED);
                consumidorS2 = new ConsumidorSemaforo(semaforo2, tanke, isRunning, 1);
                break;
            case 3: 
                System.out.println(rank+" -> "+tanke[0].getName());
                Lock mutex3 = new ReentrantLock();
                Condition condition1 = mutex3.newCondition();
                Lock mutex4 = new ReentrantLock();
                Condition condition2 = mutex4.newCondition();
                productorVC1 = new ProductorVC(tanke,mutex3,condition1, isRunning,x  + (sep * rank-1), h + 40, Color.GREEN);
                consumidorVC1 = new ConsumidorVC(tanke, isRunning,mutex3,condition1, 2);
                productorVC2 = new ProductorVC(tanke,mutex4,condition2, isRunning,x  + (sep * rank-1), h + 40, Color.RED);
                consumidorVC2 = new ConsumidorVC(tanke, isRunning,mutex4,condition2, 2);
                break;
            case 4: 
                System.out.println(rank+" -> "+tanke[0].getName());
                productorMo1 = new  ProductorMonitor(tanke,x  + (sep * rank-1), h + 40, Color.GREEN);
                consumidorMo1 = new ConsumidorMonitor(tanke, 3);
                productorMo2 = new  ProductorMonitor(tanke,x  + (sep * rank-1), h + 40, Color.RED);
                consumidorMo2 = new ConsumidorMonitor(tanke, 3);
                break;
            case 5:
                System.out.println(rank+" -> "+tanke[0].getName());
                CyclicBarrier barrera1 = new CyclicBarrier(1);
                CyclicBarrier barrera2 = new CyclicBarrier(1);
                productorB1 = new ProductorBarreras(barrera1, tanke,  + (sep * rank-1), h + 40, Color.GREEN);
                consumidorB1 = new ConsumidorBarreras(barrera1, tanke, 4);
                productorB2 = new ProductorBarreras(barrera2, tanke,  + (sep * rank-1), h + 40, Color.RED);
                consumidorB2 = new ConsumidorBarreras(barrera2, tanke, 4);
                break;*/
        }
        
        MPI.Finalize();
        
    }
}
