
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Stack;
import mpi.MPI;

public class DibujaTank extends JPanel{    
    
    private final LiFoTanke[] tankes;
    
    int h, w, y, x, sep;

    DibujaTank(LiFoTanke[] tankes, int h,int w,int y,int x,int sep) {
        
        this.tankes = tankes;
        this.h = h;
        this.w = w;
        this.y = y;
        this.x = x;
        this.sep = sep;
        setBackground(Color.white);
        System.out.println("Hola mundo - Nucleo #"+MPI.COMM_WORLD.Rank());
    }
    
    public void actualizar(LiFoTanke tanke, int index){
      tankes[index] = tanke;
      repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            h = (int) (Toolkit.getDefaultToolkit().getScreenSize().height-Toolkit.getDefaultToolkit().getScreenSize().height*0.2);
            w = 100;
            y = 80;
            x = 50;
            sep = 125;
            g2.draw(new Rectangle2D.Double(x + (sep * 0), y, w, h));
            g2.draw(new Rectangle2D.Double(x + (sep * 1), y, w, h));
            g2.draw(new Rectangle2D.Double(x + (sep * 2), y, w, h));
            g2.draw(new Rectangle2D.Double(x + (sep * 3), y, w, h));
            g2.draw(new Rectangle2D.Double(x + (sep * 4), y, w, h));
            y = 60;
            g2.drawString(tankes[0].getName(), x + (sep * 0) + 30, y);
            g2.drawString(tankes[1].getName(), x + (sep * 1) + 10, y);
            g2.drawString(tankes[2].getName(), x + (sep * 2) - 10, y);
            g2.drawString(tankes[3].getName(), x + (sep * 3) + 25, y);
            g2.drawString(tankes[4].getName(), x + (sep * 4) + 25, y);
            x = 90;
            y = h + 100;
            g2.drawString((float) tankes[0].size() / 20 * 100 + "%", x + (sep * 0) - 10, y);
            g2.drawString((float) tankes[1].size() / 20 * 100 + "%", x + (sep * 1), y);
            g2.drawString((float) tankes[2].size() / 20 * 100 + "%", x + (sep * 2), y);
            g2.drawString((float) tankes[3].size() / 20 * 100 + "%", x + (sep * 3), y);
            g2.drawString((float) tankes[4].size() / 20 * 100 + "%", x + (sep * 4), y);

            //g2.setColor(Color.BLUE);
            Stack<Agua> t1 = (Stack<Agua>) tankes[0].clone();
            Stack<Agua> t2 = (Stack<Agua>) tankes[1].clone();
            Stack<Agua> t3 = (Stack<Agua>) tankes[2].clone();
            Stack<Agua> t4 = (Stack<Agua>) tankes[3].clone();
            Stack<Agua> t5 = (Stack<Agua>) tankes[4].clone();

            for (int i = 0; i < 20; i++) {
                if (i < t1.size()) {
                    g2.setColor(t1.get(i).getColor());
                    g2.fill(t1.get(i));
                }
                if (i < t2.size()) {
                    g2.setColor(t2.get(i).getColor());
                    g2.fill(t2.get(i));
                }
                if (i < t3.size()) {
                    g.setColor(t3.get(i).getColor());
                    g2.fill(t3.get(i));
                }
                if (i < t4.size()) {
                    g.setColor(t4.get(i).getColor());
                    g2.fill(t4.get(i));
                }
                if (i < t5.size()) {
                    g.setColor(t5.get(i).getColor());
                    g2.fill(t5.get(i));
                }
            }
        } catch (Exception e) {
            System.out.println(e);

        }
    }
}
