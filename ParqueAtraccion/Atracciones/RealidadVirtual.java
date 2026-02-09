package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class RealidadVirtual {
    // Variables de Estado.
    private final Semaphore semaforoVisores; // Cantidad de visores VR.
    private final Semaphore semaforoManoplas; // Se requieren 2 permisos.
    private final Semaphore semaforoBases; // Se requiere 1 permiso.
    private final Semaphore guardia = new Semaphore(1, true);
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
            guardia.acquire(); // Asegura que solo un visitante intente conseguir recursos a la vez.
            // Intenta conseguir todos los recursos. Si uno falla, hay que devolver los anteriores.
            
            semaforoVisores.acquire();
            try{
                visorObtenido = true;
                System.out.println("[RV]: " + nombre + " consiguió Visor.");

                semaforoManoplas.acquire(2); // Solicita 2 permisos.
                manoplasObtenidas = true;
                System.out.println("[RV]: " + nombre + " consiguió Manoplas.");

                semaforoBases.acquire();
                baseObtenida = true;
                System.out.println("[RV]: " + nombre + " inicia actividad de RV con equipo completo.");
            } finally {
                guardia.release(); // Libera el guardia para que otro visitante pueda intentar.
            }

        System.out.println("[RV]: " + nombre + " inicia actividad.");

        Thread.sleep(2500);

        System.out.println("[RV]: " + nombre + " finaliza actividad.");
        
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