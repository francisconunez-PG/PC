package ParqueAtraccion;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import hilos.Visitante;

public class Comedor {
    // Variables de Estado.
    private static final int capacidadMesa = 4;
    private static final int cantidadMesa = 5;
    private static int contador = 0;

    private final CyclicBarrier barreraInicioComida; // Sincroniza cuando la mesa se llena.
    private final Semaphore semaforoComedor; // Controla la capacidad máxima del comedor.

    public Comedor() {
        this.semaforoComedor = new Semaphore(capacidadMesa * cantidadMesa);
        
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
            contador++;
            if(contador==4){                    // Suma contador para resetear la barrera cada vez que se llena una mesa.
                barreraInicioComida.reset();
                contador=0;                    // Reinicia el contador.

            }
            semaforoComedor.release();
            
            
        }
    }
}
