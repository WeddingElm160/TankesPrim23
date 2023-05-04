
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

public class XYLineChart extends JPanel implements ChangeListener {

    protected JFreeChart jfreechart;
    CategoryPlot plot;
    private Queue<Integer> cuentaProductor, cuentaConsumidor;
    private JSlider slider;

    public XYLineChart(Queue<Integer> cuentaProductor, Queue<Integer> cuentaConsumidor, String name) {
        super(new BorderLayout());
        this.cuentaProductor = cuentaProductor;
        this.cuentaConsumidor = cuentaConsumidor;

        //se declara el grafico XY Lineal
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        jfreechart = ChartFactory.createBarChart(
                name, "Tiempo", "Ejecuciones",
                dataset, PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(jfreechart);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        chartPanel.setPreferredSize(new java.awt.Dimension((screen.width - 750) / 2, (screen.height - 70) / 3));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        chartPanel.setBorder(border);
        add(chartPanel);

        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));

        slider = new JSlider(40, 100, 100);
        slider.addChangeListener(this);
        slider.setEnabled(false);
        dashboard.add(slider);
        add(dashboard, BorderLayout.SOUTH);

        //personalización del grafico
        plot = (CategoryPlot) jfreechart.getPlot();
        plot.setBackgroundPaint(Color.white);
        //plot.setDomainGridlinePaint( Color.BLACK );
        //plot.setRangeGridlinePaint( Color.BLACK );
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelsVisible(false);

        /*NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRange(true);
        yAxis.setRange(new Range(0.5, null));*/
        // -> Pinta Shapes en los puntos dados por el XYDataset
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        //--> muestra los valores de cada punto XY
        CategoryItemLabelGenerator xy = new StandardCategoryItemLabelGenerator();
        renderer.setBaseItemLabelGenerator(xy);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER, TextAnchor.CENTER, -Math.PI / 2));
        renderer.setItemMargin(0); // Aqui se modifica el margen
        renderer.setMaximumBarWidth(0.9); // El ancho maximo de las barras

        //domainAxis.setRange(new DateRange(0,20));
        //fin de personalización
        //se crea la imagen y se asigna a la clase ImageIcon
        /*BufferedImage bufferedImage  = jfreechart.createBufferedImage( d.width, d.height);
        this.setImage(bufferedImage);*/
    }

    public void actualizar() {
        updateGraph(cuentaProductor.size());
    }

    public void updateSlider() {
        slider.setMaximum(cuentaProductor.size());
        slider.setValue(cuentaConsumidor.size());
        slider.setEnabled(true);

    }

    public void blockSlider() {
        slider.setValue(cuentaProductor.size());
        slider.setEnabled(false);
    }

    protected void updateGraph(int endInterval) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        //serie #1
        IntStream.range(endInterval - 20, endInterval)
                .forEach(idx
                        -> dataset.setValue(((LinkedList<Integer>) cuentaProductor).get(idx), "Productor", "" + idx)
                );
        //serie #2
        IntStream.range(endInterval - 20, endInterval)
                .forEach(idx
                        -> dataset.setValue(((LinkedList<Integer>) cuentaConsumidor).get(idx), "Consumidor", "" + idx)
                );

        plot.setDataset(dataset);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        SwingUtilities.getWindowAncestor(slider).requestFocusInWindow();
        updateGraph(slider.getValue());
    }
}
