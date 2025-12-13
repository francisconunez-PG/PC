import java.util.concurrent.Semaphore;

public class RealidadVirtual {
    // Variables de Estado.
    private final Semaphore semaforoVisores; // Cantidad de visores VR.
    private final Semaphore semaforoManoplas; // Se requieren 2 permisos.
    private final Semaphore semaforoBases; // Se requiere 1 permiso.

    public RealidadVirtual(int cantVisores, int cantManoplas, int cantBases) {
        this.semaforoVisores = new Semaphore(cantVisores);
        this.semaforoManoplas = new Semaphore(cantManoplas); // Implementar if multiplo de 2.
        this.semaforoBases = new Semaphore(cantBases);
    }

    public void participar(Visitante visitante) {
        String nombre = visitante.getNombre();
        
        boolean visorObtenido = false;
        boolean manoplasObtenidas = false;
        boolean baseObtenida = false;

        try {
            // Intenta conseguir todos los recursos. Si uno falla, hay que devolver los anteriores.
            
            semaforoVisores.acquire();
            visorObtenido = true;
            System.out.println("[RV]: " + nombre + " consiguió Visor.");

            semaforoManoplas.acquire(2); // Solicita 2 permisos.
            manoplasObtenidas = true;
            System.out.println("[RV]: " + nombre + " consiguió Manoplas.");

            semaforoBases.acquire();
            baseObtenida = true;
            System.out.println("[RV]: " + nombre + " inicia actividad de RV con equipo completo.");

            Thread.sleep(2500);

            System.out.println("[RV]: " + nombre + " finaliza actividad de RV.");

        } catch (InterruptedException e) {
            System.out.println("[RV]: " + nombre + " interrumpido. Devolviendo equipo.");
            Thread.currentThread().interrupt();
        } finally {
            // Devuelve solo lo que se pudo obtener para evitar errores de semáforo.
            if (visorObtenido) semaforoVisores.release();
            if (manoplasObtenidas) semaforoManoplas.release(2);
            if (baseObtenida) semaforoBases.release();
        }
    }
}