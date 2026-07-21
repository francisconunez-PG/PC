package ParqueAtraccion;

import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Comedor implements Runnable {
    private final Parque parque;
    private final int capacidadMesas = 10;
    private final Semaphore mesas = new Semaphore(capacidadMesas, true);
    private final CyclicBarrier barreraLimpieza = new CyclicBarrier(capacidadMesas, this::limpiarMesas);

    public Comedor(Parque parque) {
        this.parque = parque;
    }

    // Ciclo para ingresar a comer.
    public void comer(Visitante visitante) {
        if (sentarse(visitante)) {
            consumirAlimento();
            levantarse();
        }
    }

    // Intenta conseguir un permiso de mesa.
    private boolean sentarse(Visitante visitante) {
        boolean exito = false;
        try {
            if (parque.estanActividadesAbiertas()) {
                mesas.acquire();
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " se sentó a comer.");
                exito = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return exito;
    }

    // Simula el tiempo comiendo.
    private void consumirAlimento() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Se levanta y espera a la limpieza si corresponde.
    private void levantarse() {
        try {
            barreraLimpieza.await();
            mesas.release();
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Tarea ejecutada por la CyclicBarrier.
    private void limpiarMesas() {
        System.out.println("[COMEDOR]: Se vaciaron las mesas. Personal limpiando el salón.");
    }

    // Hilo auxiliar (opcional en este caso, se usa para cierre).
    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        barreraLimpieza.reset();
    }
}