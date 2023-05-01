
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

    public LiFoTanke(String name) {
        super();
        this.name = name;
    }

    public LiFoTanke(String name, Queue<Integer> cuentaProductor, Queue<Integer> cuentaConsumidor, Queue<Integer>[] cuentas) {
      this.name = name;
    }

    public void pushAgua(Agua agua) {
        push(agua);
    }

    public void popAgua() {
        pop();
    }

    public String getName() {
        return name;
    }
    
}
