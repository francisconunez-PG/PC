package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class BarcoPirata implements Runnable {
    private final Lock lock = new ReentrantLock(true);
    private final Condition esperaFila = lock.newCondition();
    private final Condition esperaViaje = lock.newCondition();
    private final Condition esperaMaquina = lock.newCondition();
    
    private int pasajerosAbordo = 0;
    private int pasajerosQueBajaron = 0;
    private boolean enViaje = false;
    private final int capacidad = 20;
    private final Parque parque;

    public BarcoPirata(Parque parque) {
        this.parque = parque;
    }

    // Ciclo de vida del visitante en el juego.
    public void subir(Visitante visitante) {
        boolean pudoSubir = false;
        boolean completoViaje = false;
        
        lock.lock();
        try {
            if (intentarHacerFila(visitante)) {
                pudoSubir = abordarBarco(visitante);
                if (pudoSubir) {
                    completoViaje = aguardarFinDeViaje();
                }
            }
        } finally {
            lock.unlock();
        }

        if (pudoSubir && completoViaje) {
            descenderDelBarco(visitante);
        }
    }

    // Lógica de espera en la fila con timeout.
    private boolean intentarHacerFila(Visitante visitante) {
        boolean exito = false;
        long tiempoRestante = TimeUnit.SECONDS.toNanos(3);
        boolean seCanso = false;
        
        try {
            while ((pasajerosAbordo >= capacidad || enViaje) && !seCanso && parque.estanActividadesAbiertas()) {
                if (tiempoRestante <= 0) {
                    seCanso = true;
                } else {
                    tiempoRestante = esperaFila.awaitNanos(tiempoRestante);
                }
            }

            if (seCanso || !parque.estanActividadesAbiertas()) {
                if (!parque.estanActividadesAbiertas()) esperaFila.signalAll();
            } else {
                exito = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return exito;
    }

    // Proceso de abordaje y aviso a la máquina.
    private boolean abordarBarco(Visitante visitante) {
        pasajerosAbordo++;
        System.out.println("[BARCO]: " + visitante.getNombre() + " subió (" + pasajerosAbordo + "/" + capacidad + ")");
        if (pasajerosAbordo == capacidad) {
            esperaMaquina.signal();
        }
        return true;
    }

    // Bloqueo hasta que la máquina termine el recorrido.
    private boolean aguardarFinDeViaje() {
        boolean completado = false;
        try {
            while ((enViaje || pasajerosAbordo < capacidad) && parque.estanActividadesAbiertas()) {
                esperaViaje.await();
            }
            completado = parque.estanActividadesAbiertas();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return completado;
    }

    // Proceso de salida y reinicio del ciclo.
    private void descenderDelBarco(Visitante visitante) {
        lock.lock();
        try {
            pasajerosQueBajaron++;
            if (pasajerosQueBajaron == capacidad) {
                pasajerosAbordo = 0;
                pasajerosQueBajaron = 0;
                enViaje = false;
                esperaMaquina.signal();
                esperaFila.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    // Ciclo de vida de la máquina del barco.
    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            if (esperarLlenado()) {
                simularMovimiento();
                detenerBarco();
            }
        }
        liberarVisitantesAtrapados();
    }

    // Espera a que se llene la capacidad.
    private boolean esperarLlenado() {
        boolean listo = false;
        lock.lock();
        try {
            while (pasajerosAbordo < capacidad && parque.estanActividadesAbiertas()) {
                esperaMaquina.await(1, TimeUnit.SECONDS);
            }
            if (parque.estanActividadesAbiertas()) {
                enViaje = true;
                listo = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return listo;
    }

    // Simula la duración del juego.
    private void simularMovimiento() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Detiene el barco y espera la evacuación.
    private void detenerBarco() {
        lock.lock();
        try {
            esperaViaje.signalAll();
            while (pasajerosQueBajaron < capacidad && pasajerosAbordo == capacidad && parque.estanActividadesAbiertas()) {
                esperaMaquina.await(500, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    // Desbloquea a todos al cerrar el parque.
    private void liberarVisitantesAtrapados() {
        lock.lock();
        try {
            esperaViaje.signalAll();
            esperaFila.signalAll();
        } finally {
            lock.unlock();
        }
    }
}