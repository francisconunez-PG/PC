package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class RealidadVirtual {

    private static final int visoresDisponibles = 10;
    private static final int manoplasDisponibles = 10;

    private final Semaphore visores;
    private final Semaphore manoplas;
    
    // Semáforo para que pasen de a uno a buscar el equipo y no se traben.
    private final Semaphore guardia;
    private final Parque parque;

    public RealidadVirtual(Parque parque) {
        this.parque = parque;
        this.visores = new Semaphore(visoresDisponibles, true);
        this.manoplas = new Semaphore(manoplasDisponibles, true);
        this.guardia = new Semaphore(1, true);
    }

    public void jugar(Visitante visitante) {
        String nombre = visitante.getNombre();
        
        boolean tieneVisor = false;
        boolean tieneManopla = false;

        try {
            System.out.println("[RV]: " + nombre + " hace fila para agarrar equipo.");

            guardia.tryAcquire();
            try {
                // Verifico que haya equipo completo antes de agarrar.
                if (visores.availablePermits() > 0 && manoplas.availablePermits() > 0) {
                    visores.tryAcquire();
                    tieneVisor = true;
                    
                    manoplas.tryAcquire();
                    tieneManopla = true;
                    
                    System.out.println("[RV]: " + nombre + " consiguio visor y manopla. Entrando al juego...");
                } else {
                    System.out.println("[RV]: " + nombre + " vio que faltan equipos y se fue.");
                }
            } finally {
                // Libero al guardia rápido para que pase el siguiente.
                guardia.release();
            }

            // A jugar, solo si consiguió las dos cosas.
            if (tieneVisor && tieneManopla) {
                System.out.println("[RV]: " + nombre + " esta jugando en la sala VR.");
                Thread.sleep(3000); // Simula el tiempo de juego.
                System.out.println("[RV]: " + nombre + " termino de jugar. Devolviendo equipo...");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Devuelvo las cosas para el que sigue.
            if (tieneManopla) {
                manoplas.release();
            }
            if (tieneVisor) {
                visores.release();
            }
        }
    }
}