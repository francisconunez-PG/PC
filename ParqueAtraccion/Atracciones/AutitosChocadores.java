package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AutitosChocadores {

    private final Semaphore autos = new Semaphore(20, true);
    private final Parque parque;

    // Ahora recibe el parque por constructor
    public AutitosChocadores(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        String nombre = visitante.getNombre();
        boolean consiguioAuto = false;

        try {
            if (parque.estanActividadesAbiertas()) {
                System.out.println("[AUTITOS]: " + nombre + " hace fila para los autitos.");

                // Intenta subir. Si no puede en 2 segundos, mira el reloj.
                while (!consiguioAuto && parque.estanActividadesAbiertas()) {
                    consiguioAuto = autos.tryAcquire(2, TimeUnit.SECONDS);
                }

                if (consiguioAuto) {
                    System.out.println("[AUTITOS]: " + nombre + " se subió a un auto y está chocando.");
                    Thread.sleep(3000); // Tiempo de juego
                    System.out.println("[AUTITOS]: " + nombre + " terminó de chocar y devolvió el auto.");
                } else {
                    System.out.println("[AUTITOS]: " + nombre + " se quedó sin jugar porque cerraron los autitos.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (consiguioAuto) {
                autos.release(); // Libera el auto sí o sí
            }
        }
    }
}