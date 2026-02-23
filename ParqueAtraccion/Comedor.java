package ParqueAtraccion;

import hilos.Visitante;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Comedor {

    // Capacidad de mesas en el patio de comidas.
    private final Semaphore mesas = new Semaphore(15, true);
    private final Parque parque;

    public Comedor(Parque parque) {
        this.parque = parque;
    }

    public void comer(Visitante visitante) {
        String nombre = visitante.getNombre();
        boolean consiguioMesa = false;

        try {
            // Chequea que el parque siga funcionando antes de hacer fila.
            if (parque.estanActividadesAbiertas()) {
                System.out.println("[COMEDOR]: " + nombre + " hace fila para buscar una mesa.");

                // Intenta agarrar mesa por un rato.
                while (!consiguioMesa && parque.estanActividadesAbiertas()) {
                    consiguioMesa = mesas.tryAcquire(2, TimeUnit.SECONDS);
                }

                if (consiguioMesa) {
                    System.out.println("[COMEDOR]: " + nombre + " se sento a comer.");
                    Thread.sleep(3000); // Come tranquilo.
                    System.out.println("[COMEDOR]: " + nombre + " termino su comida y libera la mesa.");
                } else {
                    System.out.println("[COMEDOR]: " + nombre + " se quedo sin comer porque cerraron.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Suelta la mesa solo si la usó.
            if (consiguioMesa) {
                mesas.release();
            }
        }
    }
}


