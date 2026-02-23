package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MontanaRusa {

    // Supongamos que el carrito tiene 8 lugares (podés cambiar el número)
    private final Semaphore carrito = new Semaphore(8, true);
    private final Parque parque;

    public MontanaRusa(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();
        boolean consiguioLugar = false;

        try {
            // Solo hace fila si está abierto
            if (parque.estanActividadesAbiertas()) {
                System.out.println("[MONTAÑA RUSA]: " + nombre + " hace fila para subir al carrito.");

                // Intenta subir. Si pasan 2 segundos, vuelve a mirar la hora.
                while (!consiguioLugar && parque.estanActividadesAbiertas()) {
                    consiguioLugar = carrito.tryAcquire(2, TimeUnit.SECONDS);
                }

                if (consiguioLugar) {
                    System.out.println("[MONTAÑA RUSA]: " + nombre + " se subió a la Montaña Rusa. ¡Aaaaaah!");
                    Thread.sleep(3000); // Duración del viaje
                    System.out.println("[MONTAÑA RUSA]: " + nombre + " se bajó mareado pero feliz.");
                } else {
                    System.out.println("[MONTAÑA RUSA]: " + nombre + " se fue de la fila porque cerraron.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (consiguioLugar) {
                carrito.release();
            }
        }
    }
}