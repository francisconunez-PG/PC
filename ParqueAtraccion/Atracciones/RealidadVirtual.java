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
    private final Parque parque;

    public RealidadVirtual(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        boolean tieneEquipo = false;
        try {
            guardia.acquire(); // El guardia controla el acceso a los equipos para evitar que varios visitantes intenten tomar el mismo equipo al mismo tiempo.
            try {
                System.out.println("[RV]: " + visitante.getNombre() + " intenta jugar.");
                if (visores.availablePermits() >= 1 && manoplas.availablePermits() >= 2 && bases.availablePermits() >= 1) {
                visores.acquire(1);
                manoplas.acquire(2);
                bases.acquire(1);
                tieneEquipo = true;
            }
            } finally {
                guardia.release(); // Asegura que el guardia siempre libere el permiso, incluso si ocurre una excepción.
            }
            
            if (tieneEquipo) {
                try{
                    System.out.println("[RV]: " + visitante.getNombre() + " está jugando.");
                    Thread.sleep(3000); // Simula el tiempo que el visitante pasa jugando.
                }finally {
                    visores.release(1);
                    manoplas.release(2);
                    bases.release(1);
                    System.out.println("[RV]: " + visitante.getNombre() + " ha terminado de jugar.");
                }
            } else {
                System.out.println("[RV]: " + visitante.getNombre() + " no encontró equipo.");
            }
        } catch (InterruptedException e) {  System.out.println("[RV]: " + visitante.getNombre() + " fue interrumpido.");
                                            Thread.currentThread().interrupt(); }
    }
}