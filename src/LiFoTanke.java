
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * se va a almacenar el agua (en cola) se empila y se desempila
 */
public class LiFoTanke extends Stack<Agua> {
    private String name;

    //paso 1: agregar el agua en una fila
    private Queue<Integer> cuentaProductor, cuentaConsumidor;
    private Queue<Integer> [] cuentas;

    public LiFoTanke(String name) {
        super();
        this.name = name;
        cuentas = new Queue[4];
        for(int i = 0; i<cuentas.length; i++)
            cuentas[i] = new LinkedList<>(Collections.nCopies(20, 0));
        
        cuentaProductor = new LinkedList<>(Collections.nCopies(20, 0));
        cuentaConsumidor = new LinkedList<>(Collections.nCopies(20, 0));
    }

    public LiFoTanke(String name, Queue<Integer> cuentaProductor, Queue<Integer> cuentaConsumidor, Queue<Integer>[] cuentas) {
      this.name = name;
      this.cuentaProductor = cuentaProductor;
      this.cuentaConsumidor = cuentaConsumidor;
      this.cuentas = cuentas;
    }
    
    /*@Override
    public LiFoTanke clone(){
      LiFoTanke tanke = new LiFoTanke(name);
      tanke.addAll(this);
      return tanke;
    }*/

    public void pushAgua(Agua agua) {
        push(agua);
    }

    public void popAgua() {
        pop();
    }

    public Queue<Integer> getCuentaProductor() {
        return cuentaProductor;
    }

    public Queue<Integer> getCuentaConsumidor() {
        return cuentaConsumidor;
    }
    
    void desplazar(int auxP, int auxC){
        cuentaProductor.add(auxP);
        cuentaConsumidor.add(auxC);
    }
    
    public Queue<Integer>[] getCuentas() {
        return cuentas;
    }

    public String getName() {
        return name;
    }
    
}

/*class tankePkg {
  public int size;
  public String name;
  public 
  tankePkg(){}
}*/