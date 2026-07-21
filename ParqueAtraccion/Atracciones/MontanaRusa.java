package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;

public class MontanaRusa implements Runnable {
    private final Parque parque;
    private int asientosOcupados = 0;
    private int pasajerosQueBajaron = 0;
    private boolean enRecorrido = false;
    private final int capacidadCarrito = 10;

    public MontanaRusa(Parque parque) {
        this.parque = parque;
    }

    // Ciclo de vida del visitante en la montaña.
    public void subir(Visitante visitante) {
        boolean pudoAbordar = false;

        synchronized (this) {
            pudoAbordar = intentarAbordar(visitante);
            if (pudoAbordar) {
                esperarFinDelRecorrido();
            }
        }

        if (pudoAbordar) {
            bajarDelCarrito(visitante);
        }
    }

    // Gestiona el abordaje y avisa cuando está lleno.
    private boolean intentarAbordar(Visitante visitante) {
        boolean exito = false;
        try {
            while ((asientosOcupados >= capacidadCarrito || enRecorrido) && parque.estanActividadesAbiertas()) {
                this.wait(1000);
            }
            if (parque.estanActividadesAbiertas()) {
                asientosOcupados++;
                if (asientosOcupados == capacidadCarrito) {
                    this.notifyAll();
                }
                exito = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return exito;
    }

    // Bloquea al pasajero hasta que termine la vuelta.
    private void esperarFinDelRecorrido() {
        try {
            while ((enRecorrido || asientosOcupados < capacidadCarrito) && parque.estanActividadesAbiertas()) {
                this.wait(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Gestiona la salida y reinicia el carrito.
    private void bajarDelCarrito(Visitante visitante) {
        synchronized (this) {
            pasajerosQueBajaron++;
            if (pasajerosQueBajaron == capacidadCarrito) {
                asientosOcupados = 0;
                pasajerosQueBajaron = 0;
                enRecorrido = false;
                this.notifyAll();
            }
        }
    }

    // Ciclo de vida del carrito.
    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            if (esperarPasajeros()) {
                realizarViaje();
                finalizarViaje();
            }
        }
        liberarPasajerosRestantes();
    }

    // Mantiene el carrito detenido hasta llenarse.
    private synchronized boolean esperarPasajeros() {
        boolean listo = false;
        try {
            while (asientosOcupados < capacidadCarrito && parque.estanActividadesAbiertas()) {
                this.wait(1000);
            }
            if (parque.estanActividadesAbiertas()) {
                enRecorrido = true;
                listo = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return listo;
    }

    // Simula el tiempo en la vía.
    private void realizarViaje() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Frena el carrito y habilita la bajada.
    private synchronized void finalizarViaje() {
        this.notifyAll();
        try {
            while (pasajerosQueBajaron < capacidadCarrito && asientosOcupados == capacidadCarrito && parque.estanActividadesAbiertas()) {
                this.wait(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Evita bloqueos al cerrar el parque.
    private synchronized void liberarPasajerosRestantes() {
        this.notifyAll();
    }
}