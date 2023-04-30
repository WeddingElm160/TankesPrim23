
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Actualizar implements ActionListener{
    private DibujaTank tanke;

    public Actualizar(DibujaTank tanke) {
        this.tanke = tanke;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tanke.repaint();
    }
    
}
