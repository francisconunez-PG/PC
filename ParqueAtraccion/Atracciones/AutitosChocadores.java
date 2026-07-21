package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class AutitosChocadores implements Runnable {
    private final Parque parque;
    private final int cantAutos = 20;
    private final Semaphore autosDisponibles = new Semaphore(cantAutos, true);
    private final Semaphore avisoArranque = new Semaphore(0);
    private final Semaphore viajeTerminado = new Semaphore(0);
    private final CyclicBarrier barreraSalida = new CyclicBarrier(cantAutos);

    public AutitosChocadores(Parque parque) {
        this.parque = parque;
    }

    // Ciclo del visitante en los autitos.
    public void subir(Visitante visitante) {
        if (tomarAuto(visitante)) {
            avisarAlOperador();
            esperarFinDeTurno();
            devolverAuto(visitante);
        }
    }

    // Intenta adquirir un permiso del semáforo.
    private boolean tomarAuto(Visitante visitante) {
        boolean tomado = false;
        try {
            if (parque.estanActividadesAbiertas()) {
                autosDisponibles.acquire();
                if (parque.estanActividadesAbiertas()) {
                    System.out.println("[AUTITOS]: " + visitante.getNombre() + " se subió a un autito.");
                    tomado = true;
                } else {
                    autosDisponibles.release();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return tomado;
    }

    // Notifica que hay un auto ocupado listo para iniciar.
    private void avisarAlOperador() {
        avisoArranque.release();
    }

    // Se bloquea hasta que la máquina libera el viajeTerminado.
    private void esperarFinDeTurno() {
        try {
            viajeTerminado.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Sincroniza la salida de los 20 autos al mismo tiempo.
    private void devolverAuto(Visitante visitante) {
        try {
            barreraSalida.await();
            autosDisponibles.release();
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Ciclo de vida del operador de la pista.
    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            if (esperarQueSeLleneLaPista()) {
                simularChoques();
                finalizarTurno();
            }
        }
        liberarAutos();
    }

    // Espera a que se ocupen todos los permisos.
    private boolean esperarQueSeLleneLaPista() {
        boolean listo = false;
        try {
            avisoArranque.acquire(cantAutos);
            if (parque.estanActividadesAbiertas()) {
                System.out.println("[AUTITOS]: La pista está llena. ¡Comienzan los choques!");
                listo = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return listo;
    }

    // Simula la duración del turno.
    private void simularChoques() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Libera a los visitantes para que usen la barrera.
    private void finalizarTurno() {
        System.out.println("[AUTITOS]: Fin del turno. Todos devuelven su auto.");
        viajeTerminado.release(cantAutos);
    }

    // Destraba bloqueos residuales.
    private void liberarAutos() {
        viajeTerminado.release(cantAutos);
        barreraSalida.reset();
    }
}