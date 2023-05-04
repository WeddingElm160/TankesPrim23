import java.awt.BasicStroke;
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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;


public class XYLineChartAll extends JPanel implements ChangeListener{
    protected JFreeChart jfreechart;
    CategoryPlot plot;
    Queue<Integer>[] volumenTankes;
    private JSlider slider;
    
    public XYLineChartAll(Queue<Integer>[] volumenTankes){
        super(new BorderLayout());
        this.volumenTankes = volumenTankes;
        
        //se declara el grafico XY Lineal
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        jfreechart = ChartFactory.createLineChart(
        "Todos" , "Tiempo", "Ejecuciones",  
        dataset, PlotOrientation.VERTICAL,  true, true, false);
        
        ChartPanel chartPanel = new ChartPanel(jfreechart);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        chartPanel.setPreferredSize(new java.awt.Dimension((screen.width-750)/2, (screen.height-70)/3));
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
        plot.setBackgroundPaint( Color.white );
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelsVisible(false);
        
        // -> Pinta Shapes en los puntos dados por el XYDataset
        LineAndShapeRenderer renderer = (LineAndShapeRenderer)  plot.getRenderer();
        
        //--> muestra los valores de cada punto XY
        CategoryItemLabelGenerator xy = new StandardCategoryItemLabelGenerator();
        renderer.setBaseItemLabelGenerator( xy );
        renderer.setStroke(new BasicStroke(3.0F));
        renderer.setBaseItemLabelsVisible(true);
        renderer.setPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER, TextAnchor.CENTER, -Math.PI / 2));
        
    }
    
    public void actualizar(){
        updateGraph(volumenTankes[0].size());
    }
    
    public void updateSlider(){
        slider.setMaximum(volumenTankes[0].size());
        slider.setValue(volumenTankes[0].size());
        slider.setEnabled(true);
        
    }
    
    public void blockSlider(){
        slider.setValue(volumenTankes[0].size());
        slider.setEnabled(false);
    }
    
    protected void updateGraph (int endInterval){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        
        
        //serie #1
        IntStream.range(endInterval-20, endInterval)
        .forEach(idx ->
                dataset.setValue( ((LinkedList<Integer>)volumenTankes[0]).get(idx),"Mutex", ""+idx)
        );
        //serie #2
        IntStream.range(endInterval-20, endInterval)
        .forEach(idx ->
                dataset.setValue( ((LinkedList<Integer>)volumenTankes[1]).get(idx),"Semaforos", ""+idx)
        );
        //serie #2
        IntStream.range(endInterval-20, endInterval)
        .forEach(idx ->
                dataset.setValue( ((LinkedList<Integer>)volumenTankes[2]).get(idx),"Variables de condición", ""+idx)
        );
        //serie #2
        IntStream.range(endInterval-20, endInterval)
        .forEach(idx ->
                dataset.setValue( ((LinkedList<Integer>)volumenTankes[3]).get(idx),"Monitores", ""+idx)
        );
        //serie #2
        IntStream.range(endInterval-20, endInterval)
        .forEach(idx ->
                dataset.setValue( ((LinkedList<Integer>)volumenTankes[4]).get(idx),"Barreras", ""+idx)
        );
                
        
        plot.setDataset(dataset);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        SwingUtilities.getWindowAncestor(slider).requestFocusInWindow();
        updateGraph(slider.getValue());
    }
    /**
 * Datos
 */

    

}//-->fin clase