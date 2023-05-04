
import java.util.Queue;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GraficasPanel extends JPanel {// Ésta es la clase principal es un JFrame...

    private XYLineChart[] charts;
    private XYLineChartAll chartAll;

    // El panel principal donde se pone todo Constructor de la clase: se configura aquí toda  la ventana y los controles...
    public GraficasPanel(Queue<Integer>[] cuentaProductor, Queue<Integer>[] cuentaConsumidor, Queue<Integer>[] volumenTankes,String[] names) {
        super();
        setBorder(new EmptyBorder(5, 5, 5, 5));
        charts = new XYLineChart[5];//Se crea un arreglo de gráficas de 
        
        for (int i = 0; i < 5; i++) {//Se instancia el arreglo de graficas
            charts[i] = new XYLineChart(cuentaProductor[i], cuentaConsumidor[i], names[i]);
            add(charts[i]);
        }
        chartAll = new XYLineChartAll(volumenTankes); // se instancia el panel de gráfica de todos los elementos
        add(chartAll);

    }

    public void pausarGrafica() {//función para pausar las gráficas
        for (XYLineChart chart : charts) {
            chart.updateSlider();
        }
        chartAll.updateSlider();
    }

    public void reanudarGrafica() {//función para reanudar las gráficas
        for (XYLineChart chart : charts) {
            chart.blockSlider();
        }
        chartAll.blockSlider();
    }

    public void actualizarGrafica(int n) {//función para actualizar una gráfica del panel
        charts[n].actualizar();
    }
    public void actualizarTodasGraficas() {//función para actualizar todas las gráfica del panel
        for (XYLineChart chart : charts) {
            chart.actualizar();
        }
        chartAll.actualizar();
    }
    
}
