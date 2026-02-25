package ParqueAtraccion.Atracciones;
import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class RealidadVirtual {
    //Semaphore para controlar el acceso a los equipos de realidad virtual.
    private final Semaphore visores = new Semaphore(10, true);
    private final Semaphore manoplas = new Semaphore(20, true);
    private final Semaphore bases = new Semaphore(10, true);
    private final Semaphore guardia = new Semaphore(1, true);

    public RealidadVirtual(Parque parque) {
        //TODO Auto-generated constructor stub
    }

    public void jugar(Visitante visitante) {
        boolean tieneEquipo = false;
        try {
            guardia.acquire();
            if (visores.availablePermits() >= 1 && manoplas.availablePermits() >= 2 && bases.availablePermits() >= 1) {
                visores.acquire(1);
                manoplas.acquire(2);
                bases.acquire(1);
                tieneEquipo = true;
            }
            guardia.release();

            if (tieneEquipo) {
                System.out.println("[RV]: " + visitante.getNombre() + " está jugando.");
                Thread.sleep(3000);
                visores.release(1);
                manoplas.release(2);
                bases.release(1);
            } else {
                System.out.println("[RV]: " + visitante.getNombre() + " no encontró equipo.");
            }
        } catch (InterruptedException e) {  System.out.println("[RV]: " + visitante.getNombre() + " fue interrumpido.");
                                            Thread.currentThread().interrupt(); }
    }
}