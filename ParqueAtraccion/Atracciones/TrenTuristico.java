package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.ArrayBlockingQueue;

public class TrenTuristico implements Runnable {
    private final Parque parque;
    private final int capacidadTren = 15;
    private final ArrayBlockingQueue<Visitante> estacion = new ArrayBlockingQueue<>(capacidadTren);
    private int pasajerosAbordo = 0;

    public TrenTuristico(Parque parque) {
        this.parque = parque;
    }

    // Ciclo del visitante esperando el tren.
    public void viajar(Visitante visitante) {
        if (hacerFilaEnEstacion(visitante)) {
            esperarRecorrido();
        }
    }

    // Ingresa a la cola y espera ser llamado.
    private boolean hacerFilaEnEstacion(Visitante visitante) {
        boolean exito = false;
        try {
            estacion.put(visitante);
            synchronized (this) {
                while (estacion.contains(visitante) && parque.estanActividadesAbiertas()) {
                    this.wait(1000);
                }
            }
            exito = parque.estanActividadesAbiertas();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return exito;
    }

    // Se mantiene a bordo hasta que el tren finalice.
    private synchronized void esperarRecorrido() {
        try {
            while (pasajerosAbordo > 0 && parque.estanActividadesAbiertas()) {
                this.wait(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Ciclo del tren.
    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            if (subirPasajeros()) {
                hacerRecorrido();
                bajarPasajeros();
            }
        }
    }

    // Transfiere de la cola al tren.
    private synchronized boolean subirPasajeros() {
        boolean listo = false;
        try {
            while (estacion.size() < capacidadTren && parque.estanActividadesAbiertas()) {
                this.wait(1000);
            }
            if (parque.estanActividadesAbiertas()) {
                estacion.clear();
                pasajerosAbordo = capacidadTren;
                System.out.println("[TREN]: Partiendo con " + capacidadTren + " pasajeros.");
                this.notifyAll();
                listo = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return listo;
    }

    // Simula el paseo por el parque.
    private void hacerRecorrido() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Vacía el tren y avisa a los que esperan bajar.
    private synchronized void bajarPasajeros() {
        System.out.println("[TREN]: Llegó a la estación. Todos descienden.");
        pasajerosAbordo = 0;
        this.notifyAll();
    }
}