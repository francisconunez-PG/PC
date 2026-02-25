package ParqueAtraccion;
import hilos.Visitante;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Comedor {
    //CyclicBarrier para esperar a 4 visitantes por mesa, con un timeout de 5 segundos para evitar esperas eternas
    private final CyclicBarrier mesa = new CyclicBarrier(4, () -> {
        System.out.println("[COMEDOR]: Mesa de 4 completa. Empiezan a comer.");
    });
    private final Parque parque;

    public Comedor(Parque parque) { this.parque = parque; }

    public void comer(Visitante visitante) {
        boolean pudoSentarse = false;
        try {
            if (parque.estanActividadesAbiertas()) {
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " busca lugar.");
                mesa.await(5, TimeUnit.SECONDS);
                pudoSentarse = true; // Si pudo sentarse, espera a que se complete la mesa o se alcance el timeout
            }
        } catch (Exception e) {
            System.out.println("[COMEDOR]: " + visitante.getNombre() + " se fue por falta de compañeros.");
        }

        if (pudoSentarse) {
            try {
                Thread.sleep(2500);
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " terminó de comer.");
            } catch (InterruptedException e) {  System.out.println("[COMEDOR]: " + visitante.getNombre() + " fue interrumpido.");
                                                Thread.currentThread().interrupt();}
        }
    }
}