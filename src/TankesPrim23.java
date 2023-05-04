import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

public class TankesPrim23 extends JFrame {

    private boolean isRunning;//Variable de pausa
    private DibujaTank panelTank;//Panel de tanques
    private static GraficasPanel graficas;//Panel de gráficas
    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); //variables que nos proporsiona las dimensiones de la pantalla para hacerla adaptable a cualquier pantalla
    static int h = (int) (Toolkit.getDefaultToolkit().getScreenSize().height - Toolkit.getDefaultToolkit().getScreenSize().height * 0.2),
            w = 100, y = 80, x = 50, sep = 125;// tamaños de tanques
    private static ProductorMutex productorM1, productorM2;//declaración de productores y consumidores
    private static ConsumidorMutex consumidorM1, consumidorM2;
    private static ProductorSemaforo productorS1, productorS2;
    private static ConsumidorSemaforo consumidorS1, consumidorS2;
    private static ProductorVC productorVC1, productorVC2;
    private static ConsumidorVC consumidorVC1, consumidorVC2;
    private static ProductorMonitor productorMo1, productorMo2;
    private static ConsumidorMonitor consumidorMo1, consumidorMo2;
    private static ProductorBarreras productorB1, productorB2;
    private static ConsumidorBarreras consumidorB1, consumidorB2;
    private static LiFoTanke[] tankes;//Tanques de agua
    private static Queue<Integer>[] volumenTankes, cuentaProductor, cuentaConsumidor;// Cola de datos a gráficar
    private String[] names;//lista de nombres de los tanques y gráficas.

    TankesPrim23() {//Instancia de nuestro Jframe con variables
        isRunning = true;//Se establece la corrida del programa en true por defecto
        names = new String[5];// configuración de nombres
        names[0] = "Mutex";
        names[1] = "Semaforos";
        names[2] = "Variables de condición";
        names[3] = "Monitores";
        names[4] = "Barreras";
        tankes = new LiFoTanke[5];
        volumenTankes = new Queue[5];
        cuentaProductor = new Queue[5];
        cuentaConsumidor = new Queue[5];
        for (int i = 0; i < 5; i++) {//Se instancian los tanques y las variables de Cola de contabilidadde de datos a gráficar
            tankes[i] = new LiFoTanke(names[i]);
            volumenTankes[i] = new LinkedList<>(Collections.nCopies(20, 0));
            cuentaProductor[i] = new LinkedList<>(Collections.nCopies(20, 0));
            cuentaConsumidor[i] = new LinkedList<>(Collections.nCopies(20, 0));
        }
        setTitle("Tankes");//Se configura el frame principal
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        panelTank = new DibujaTank(tankes, h, w, y, x, sep);  //Se instancia el panel de tankes
        panelTank.setSize(700, screen.height);
        graficas = new GraficasPanel(cuentaProductor, cuentaConsumidor, volumenTankes, names); //S e instancia el panel de gráficas
        graficas.setBounds(700, 0, screen.width - 700, screen.height);

        add(panelTank, BorderLayout.WEST);//Se añaden los paneles al frame
        add(graficas, BorderLayout.EAST);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {// Se establece el escucha de teclas
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {//Esc para terminar el programa
                    System.exit(0);
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {//Espacio para pausar/reanudar la graficación de los hilos
                    pauseState();
                }
            }
        });
    }

    public void pauseState() {//Funcion de pausar/reanudar la graficación de los hilos
        isRunning = !isRunning;
        if (isRunning) {
            graficas.reanudarGrafica();
        } else {
            graficas.pausarGrafica();
        }
        boolean buffRun[] = new boolean[1];
        buffRun[0] = isRunning;
        for (int i = 1; i <= 5; i++) {
            MPI.COMM_WORLD.Isend(buffRun, 0, 1, MPI.BOOLEAN, i, 1);//Se notifica a todos los procesos sobre el cambio de estado de pausa/reanudación
        }
    }

    public static LiFoTanke[] getTankes() {
        return tankes;
    }

    public DibujaTank getPanelTank() {
        return panelTank;
    }

    public static void main(String[] args) throws InterruptedException { //Main
        MPI.Init(args); //Se inicia el proceso de procesos paralelos con MPI
        int rank = MPI.COMM_WORLD.Rank(); // Se obtiene el identificador de proceso

        TankesPrim23 frame;//Se declaran variables buffers para el paso de mensajes entre procesos 
        LiFoTanke tanke[] = new LiFoTanke[1];
        LiFoTanke buffTabkes[] = new LiFoTanke[6];
        int buffCount[] = new int[2];
        int recvBuffCount[] = new int[12];
        boolean buffRun[] = new boolean[1];
        buffRun[0] = true;

        if (rank == 0) {//Se establece la configuración para el proceso principal 0
            System.setProperty("sun.java2d.uiScale", "1");
            frame = new TankesPrim23(); // Se instancia la clase principal o frame
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.arraycopy(frame.getTankes(), 0, buffTabkes, 1, frame.getTankes().length);//Se Copian los tanques instanciados en el frame y se añade un tanke sin instanciar al principo del arreglo "buffTabkes" para realizar un Scatter correcto.

            for (int i = 1; i <= 5; i++) {//Se crean 5 hilos que se encargan de escuchar a todos procesos por sus tankes
                int index = i;
                new Thread(() -> {
                    while (true) {
                        MPI.COMM_WORLD.Recv(tanke, 0, 1, MPI.OBJECT, index, 0);
                        tankes[index - 1] = tanke[0];
                        frame.getPanelTank().repaint();
                    }
                }).start();
            }
        } else { // Se configura a los procesos esclavos
            new Thread(new Runnable() {//En cada proceso se crea un hilo que se encarga de escuchar el cambio de estado de la variable de pausa/reanudar
                @Override                //y este a su vez ejecutara la pausa/reanudación de sus hilos
                public void run() {
                    while (true) {
                        MPI.COMM_WORLD.Recv(buffRun, 0, 1, MPI.BOOLEAN, 0, 1);
                        switch (rank) {
                            case 1:
                                productorM1.setIsRunning(buffRun[0]);
                                consumidorM1.setIsRunning(buffRun[0]);
                                productorM2.setIsRunning(buffRun[0]);
                                consumidorM2.setIsRunning(buffRun[0]);
                                break;
                            case 2:
                                productorS1.setIsRunning(buffRun[0]);
                                consumidorS1.setIsRunning(buffRun[0]);
                                productorS2.setIsRunning(buffRun[0]);
                                consumidorS2.setIsRunning(buffRun[0]);
                                break;
                            case 3:
                                productorVC1.setIsRunning(buffRun[0]);
                                consumidorVC1.setIsRunning(buffRun[0]);
                                productorVC2.setIsRunning(buffRun[0]);
                                consumidorVC2.setIsRunning(buffRun[0]);
                                break;
                            case 4:
                                productorMo1.setIsRunning(buffRun[0]);
                                consumidorMo1.setIsRunning(buffRun[0]);
                                productorMo2.setIsRunning(buffRun[0]);
                                consumidorMo2.setIsRunning(buffRun[0]);
                                break;
                            case 5:
                                productorB1.setIsRunning(buffRun[0]);
                                consumidorB1.setIsRunning(buffRun[0]);
                                productorB2.setIsRunning(buffRun[0]);
                                consumidorB2.setIsRunning(buffRun[0]);
                                break;
                        }
                    }
                }
            }).start();
        }

        MPI.COMM_WORLD.Scatter(buffTabkes, 0, 1, MPI.OBJECT, tanke, 0, 1, MPI.OBJECT, 0); //Se realiza un scatter del proceso 0 al resto de procesos con la variable buffTabkes

        switch (rank) {//Dependiendo del identificador de proceso se instancian y activan sus respectivos hilos y metodos de sincronización
            case 1:
                System.out.println("Nucleo #" + rank + " -> " + tanke[0].getName());
                Lock mutex = new ReentrantLock();
                Lock mutex2 = new ReentrantLock();
                productorM1 = new ProductorMutex(mutex, tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.GREEN);
                consumidorM1 = new ConsumidorMutex(mutex, tanke, buffRun[0], 0);
                productorM2 = new ProductorMutex(mutex2, tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.RED);
                consumidorM2 = new ConsumidorMutex(mutex2, tanke, buffRun[0], 0);
                productorM1.start();
                consumidorM1.start();
                productorM2.start();
                consumidorM2.start();
                break;
            case 2:
                System.out.println("Nucleo #" + rank + " -> " + tanke[0].getName());
                Semaphore semaforo = new Semaphore(1, buffRun[0]);
                Semaphore semaforo2 = new Semaphore(1, buffRun[0]);
                productorS1 = new ProductorSemaforo(semaforo, tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.GREEN);
                consumidorS1 = new ConsumidorSemaforo(semaforo, tanke, buffRun[0], 1);
                productorS2 = new ProductorSemaforo(semaforo2, tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.RED);
                consumidorS2 = new ConsumidorSemaforo(semaforo2, tanke, buffRun[0], 1);
                productorS1.start();
                consumidorS1.start();
                productorS2.start();
                consumidorS2.start();
                break;
            case 3:
                System.out.println("Nucleo #" + rank + " -> " + tanke[0].getName());
                Lock mutex3 = new ReentrantLock();
                Condition condition1 = mutex3.newCondition();
                Lock mutex4 = new ReentrantLock();
                Condition condition2 = mutex4.newCondition();
                productorVC1 = new ProductorVC(tanke, mutex3, condition1, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.GREEN);
                consumidorVC1 = new ConsumidorVC(tanke, buffRun[0], mutex3, condition1, 2);
                productorVC2 = new ProductorVC(tanke, mutex4, condition2, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.RED);
                consumidorVC2 = new ConsumidorVC(tanke, buffRun[0], mutex4, condition2, 2);
                productorVC1.start();
                consumidorVC1.start();
                productorVC2.start();
                consumidorVC2.start();
                break;
            case 4:
                System.out.println("Nucleo #" + rank + " -> " + tanke[0].getName());
                productorMo1 = new ProductorMonitor(tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.GREEN);
                consumidorMo1 = new ConsumidorMonitor(tanke, buffRun[0], 3);
                productorMo2 = new ProductorMonitor(tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.RED);
                consumidorMo2 = new ConsumidorMonitor(tanke, buffRun[0], 3);
                productorMo1.start();
                consumidorMo1.start();
                productorMo2.start();
                consumidorMo2.start();
                break;
            case 5:
                System.out.println("Nucleo #" + rank + " -> " + tanke[0].getName());
                CyclicBarrier barrera1 = new CyclicBarrier(1);
                CyclicBarrier barrera2 = new CyclicBarrier(1);
                productorB1 = new ProductorBarreras(barrera1, tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.GREEN);
                consumidorB1 = new ConsumidorBarreras(barrera1, tanke, buffRun[0], 4);
                productorB2 = new ProductorBarreras(barrera2, tanke, buffRun[0], x + (sep * (rank - 1)), h + 40, Color.RED);
                consumidorB2 = new ConsumidorBarreras(barrera2, tanke, buffRun[0], 4);
                productorB1.start();
                consumidorB1.start();
                productorB2.start();
                consumidorB2.start();
                break;
        }

        MPI.Finalize();
        while (true) {// su hilo principal se convierte en un metodo que se encarga de actualizar las gráficas cada 2 seg.
            Thread.sleep(2000);//Se establece el tiempo de actualización de los graficos
            if (!buffRun[0]) {//Continue en el caso de que el estado de pausa esté activado
                continue;
            }

            switch (rank) {//Se optienen los contadores de ejecución de los hilos dependiendo del identificador de proceso guardandolo en buffCount
                case 1:
                    buffCount[0] = productorM1.getExecutionCount() + productorM2.getExecutionCount();
                    buffCount[1] = consumidorM1.getExecutionCount() + consumidorM2.getExecutionCount();
                    break;
                case 2:
                    buffCount[0] = productorS1.getExecutionCount() + productorS2.getExecutionCount();
                    buffCount[1] = consumidorS1.getExecutionCount() + consumidorS2.getExecutionCount();
                    break;
                case 3:
                    buffCount[0] = productorVC1.getExecutionCount() + productorVC2.getExecutionCount();
                    buffCount[1] = consumidorVC1.getExecutionCount() + consumidorVC2.getExecutionCount();
                    break;
                case 4:
                    buffCount[0] = productorMo1.getExecutionCount() + productorMo2.getExecutionCount();
                    buffCount[1] = consumidorMo1.getExecutionCount() + consumidorMo2.getExecutionCount();
                    break;
                case 5:
                    buffCount[0] = productorB1.getExecutionCount() + productorB2.getExecutionCount();
                    buffCount[1] = consumidorB1.getExecutionCount() + consumidorB2.getExecutionCount();
                    break;
            }
            MPI.COMM_WORLD.Gather(buffCount, 0, 2, MPI.INT, recvBuffCount, 0, 2, MPI.INT, 0); //Se realiza un gather de todos los procesos hacia el proceso 0 para controlar las cuentas de los procesos
            if (rank == 0) {//El proceso 0 se encarga de actualizar las gráficas con los elementos recopilados
                for (int i = 2; i < 12; i += 2) {
                    cuentaProductor[i / 2 - 1].add(recvBuffCount[i]);
                    cuentaConsumidor[i / 2 - 1].add(recvBuffCount[i + 1]);
                    volumenTankes[i / 2 - 1].add(tankes[i / 2 - 1].size());
                }
                graficas.actualizarTodasGraficas();//se actualizan las gráficas
            }
        }
    }
}
