import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Comedor {

    private static final int capacidadMesa = 4;
    private static final int capacidadMaxima = 20;

    private final CyclicBarrier barreraInicioComida;
    private final Semaphore semaforoComedor;

    public Comedor() {
        this.semaforoComedor = new Semaphore(capacidadMaxima);
        
        // Se ejecuta cuando la mesa se llena.
        this.barreraInicioComida = new CyclicBarrier(capacidadMesa, () ->
            System.out.println("[COMEDOR]: ¡MESA COMPLETA! Pueden empezar a comer ahora. ")
        );
    }

    public void almorzar(Visitante visitante) {
        String nombre = visitante.getNombre();
        
        try {
            if (!semaforoComedor.tryAcquire(1, TimeUnit.SECONDS)) {
                System.out.println("[COMEDOR]: " + nombre + " encontró el Comedor lleno. Se va.");
                return;
            }
            
            System.out.println("[COMEDOR]: " + nombre + " se sienta en la mesa. Esperando...");

            // Esperar la barrera (3 personas más deben llegar)
            barreraInicioComida.await();

            System.out.println("[COMEDOR]: " + nombre + " está almorzando.");
            Thread.sleep(1500);
            
            System.out.println("[COMEDOR]: " + nombre + " ha terminado de almorzar.");

        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoComedor.release();
        }
    }
}
