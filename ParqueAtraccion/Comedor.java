package ParqueAtraccion;

import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.TimeoutException;

public class Comedor {

    private static final int mesas = 5;
    private static final int capacidadMesa = 4;
    private final Semaphore lugarDisponibles;
    private final CyclicBarrier barreraMesa;

    public Comedor() {
        this.lugarDisponibles = new Semaphore(mesas * capacidadMesa, true);

        // La barrera se dispara cuando llegan 4 personas a la mesa.
        this.barreraMesa = new CyclicBarrier(capacidadMesa, () ->
                System.out.println("[COMEDOR]: --- ¡Mesa completa! Comienzan a comer. ---")
        );
    }

    // El visitante intenta sentarse, espera a que se complete la mesa o se cancele por timeout, y luego come.
    public void almorzar(Visitante visitante) {
        String nombre = visitante.getNombre();
        boolean tieneAsiento = false;

        try {
            // Intenta conseguir un asiento. Espera un poco por si se libera uno.
            if (lugarDisponibles.tryAcquire(5, SECONDS)) {
                tieneAsiento = true;
                System.out.println("[COMEDOR]: " + nombre + " se sentó. Esperando grupo...");

                try {
                    
                    // Espera a que se complete la mesa o se cancele por timeout.
                    barreraMesa.await(5, SECONDS);
                    
                    // La barrera se abre.
                    System.out.println("[COMEDOR]: " + nombre + " está comiendo...");
                    Thread.sleep(2000);

                } catch (TimeoutException e) {
                    System.out.println("[COMEDOR]: " + nombre + " se cansó de esperar y se levanta.");
                    // Al resetear, liberamos a los que quedaron colgados esperando.
                    barreraMesa.reset();
                } catch (BrokenBarrierException e) {
                    System.out.println("[COMEDOR]: " + nombre + " se retira porque la mesa se canceló (alguien se fue).");
                }
            } else {
                // No hay asientos disponibles tras la espera.
                System.out.println("[COMEDOR]: " + nombre + " no encontró sitio y se va.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (tieneAsiento) {
                lugarDisponibles.release();
            }
        }
    }
}


