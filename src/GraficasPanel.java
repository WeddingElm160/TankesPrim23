
import java.util.Queue;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// Ésta es la clase principal
// es un JFrame...
public class GraficasPanel extends JPanel {

    private XYLineChart[] charts;
    private XYLineChartAll chartAll;

    // el panel principal donde se pone todo
    // Constructor de la clase: se configura aquí toda 
    // la ventana y los controles...
    public GraficasPanel(LiFoTanke[] tankes, Queue<Integer>[] volumenTankes) {
        super();
        setBorder(new EmptyBorder(5, 5, 5, 5));
        charts = new XYLineChart[tankes.length];
        
        for (int i = 0; i < tankes.length; i++) {
            charts[i] = new XYLineChart(tankes[i]);
            add(charts[i]);
        }
        chartAll = new XYLineChartAll(volumenTankes);
        add(chartAll);

    }

    public void pausarGrafica() {
        for (XYLineChart chart : charts) {
            chart.updateSlider();
        }
        chartAll.updateSlider();
    }

    public void reanudarGrafica() {
        for (XYLineChart chart : charts) {
            chart.blockSlider();
        }
        chartAll.blockSlider();
    }

    public void actualizarGrafica(int n) {
        charts[n].actualizar();
    }
    public void actualizarTodasGraficas() {
        for (XYLineChart chart : charts) {
            chart.actualizar();
        }
        chartAll.actualizar();
    }
    
}
