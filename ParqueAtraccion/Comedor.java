package ParqueAtraccion;

import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Comedor {

    private static final int mesas = 5;
    private static final int capacidadMesa = 4;

    private final Semaphore mesasDisponibles;
    private final CyclicBarrier barreraMesa;

    public Comedor() {
        this.mesasDisponibles = new Semaphore(mesas, true);

        // La barrera se dispara cuando llegan 4 personas a la mesa.
        this.barreraMesa = new CyclicBarrier(capacidadMesa, () ->
                System.out.println("[COMEDOR]: Mesa completa. Comienzan a comer.")
        );
    }

    public void almorzar(Visitante visitante) {
        String nombre = visitante.getNombre();

        try {
            // Espera una mesa libre.
            mesasDisponibles.acquire();
            System.out.println("[COMEDOR]: " + nombre + " se sienta en una mesa.");

            try {
                // Espera a que la mesa esté completa.
                barreraMesa.await();
            } catch (BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }

            // Comer
            System.out.println("[COMEDOR]: " + nombre + " está almorzando.");
            Thread.sleep(1500);
            System.out.println("[COMEDOR]: " + nombre + " terminó de almorzar.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Libera la mesa cuando termina
            mesasDisponibles.release();
        }
    }
}


