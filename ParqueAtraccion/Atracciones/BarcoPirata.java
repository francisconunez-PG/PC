package ParqueAtraccion.Atracciones;
import hilos.Visitante;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class BarcoPirata {
    
    private final Lock lock = new ReentrantLock(true);
    private final Condition espera = lock.newCondition();
    private int pasajeros = 0;
    private boolean enViaje = false;

    public void subir(Visitante visitante) {
    //Cerrojo para controlar el acceso al barco pirata. Los visitantes intentan subir al barco, pero si está lleno o en viaje, deben esperar a que se libere un lugar o a que el barco regrese.
    //Si no pueden subir en 3 segundos, se van.
        boolean pudoSubir = false;
        lock.lock();
        try {
            if (pasajeros >= 20 || enViaje) {   // Si el barco está lleno o en viaje, el visitante espera a que se libere un lugar o a que el barco regrese.
                espera.await(3, TimeUnit.SECONDS);  //Si no puede subir en 3 segundos, se va.
            }
            if (!enViaje && pasajeros < 20) {
                pasajeros++;
                pudoSubir = true;
                if (pasajeros == 20) { enViaje = true; }
            }
        } catch (InterruptedException e) {
            System.out.println("[BARCO]: " + visitante.getNombre() + " no pudo subir y se fue.");
            Thread.currentThread().interrupt();
        } finally { lock.unlock(); }

        if (pudoSubir) {
            try { // Simula el viaje del barco pirata. Después de 3 segundos, los pasajeros bajan y el barco se marca como disponible.
                Thread.sleep(3000);
                lock.lock();
                try {
                    pasajeros--;
                    if (pasajeros == 0) {
                        enViaje = false;
                        espera.signalAll(); // Si el barco se vacía, se marca como disponible y se notifica a los visitantes en espera
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {  System.out.println("[BARCO]: " + visitante.getNombre() + " se interrumpió durante el viaje.");
                                                Thread.currentThread().interrupt(); }
        }
    }
}



