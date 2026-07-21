package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class RealidadVirtual implements Runnable {
    private final Parque parque;
    private final int capacidadSala = 5;
    private final Semaphore cascos = new Semaphore(capacidadSala, true);
    private final Semaphore jugadoresListos = new Semaphore(0);
    private final Semaphore juegoTerminado = new Semaphore(0);

    public RealidadVirtual(Parque parque) {
        this.parque = parque;
    }

    // Ciclo del jugador en la sala VR.
    public void jugar(Visitante visitante) {
        if (ponerseCasco(visitante)) {
            esperarFinDelJuego();
            devolverCasco();
        }
    }

    // Adquiere el semáforo y avisa que está listo.
    private boolean ponerseCasco(Visitante visitante) {
        boolean exito = false;
        try {
            if (parque.estanActividadesAbiertas()) {
                cascos.acquire();
                if (parque.estanActividadesAbiertas()) {
                    jugadoresListos.release();
                    exito = true;
                } else {
                    cascos.release();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return exito;
    }

    // Espera la señal del servidor para terminar.
    private void esperarFinDelJuego() {
        try {
            juegoTerminado.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Devuelve el casco para el siguiente grupo.
    private void devolverCasco() {
        cascos.release();
    }

    // Ciclo del servidor VR.
    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            int jugadoresActuales = esperarJugadores();
            if (jugadoresActuales > 0) {
                iniciarPartida(jugadoresActuales);
            }
        }
        liberarAtrapados();
    }

    // Espera hasta que haya al menos un jugador listo.
    private int esperarJugadores() {
        int reunidos = 0;
        try {
            Thread.sleep(1000);
            reunidos = jugadoresListos.drainPermits();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return reunidos;
    }

    // Corre la simulación para los jugadores reunidos.
    private void iniciarPartida(int cantidad) {
        System.out.println("[VR]: Iniciando sesión con " + cantidad + " jugadores.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        juegoTerminado.release(cantidad);
    }

    // Evita interbloqueos al cierre.
    private void liberarAtrapados() {
        juegoTerminado.release(capacidadSala);
    }
}