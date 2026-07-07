package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RealidadVirtual implements Runnable {
    // Semáforos de recursos
    private final Semaphore visores = new Semaphore(10, true);
    private final Semaphore manoplas = new Semaphore(20, true);
    private final Semaphore bases = new Semaphore(10, true);
    private final Semaphore guardia = new Semaphore(1, true);
    
    // Semáforos para sincronizar el Servidor con los Jugadores.
    private final Semaphore jugadoresListos = new Semaphore(0); //Avisa al servidor que hay jugadores listos para jugar.
    private final Semaphore finPartida = new Semaphore(0); //Avisa a los jugadores que terminó la partida y pueden devolver el equipo.
    
    private final Parque parque;

    public RealidadVirtual(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        boolean tieneEquipo = false;
        try {
            guardia.acquire();
            try {
                if (visores.availablePermits() >= 1 && manoplas.availablePermits() >= 2 && bases.availablePermits() >= 1) {
                    visores.acquire(1);
                    manoplas.acquire(2);
                    bases.acquire(1);
                    tieneEquipo = true;
                }
            } finally {
                guardia.release();
            }
            
            if (tieneEquipo) {
                System.out.println("[RV]: " + visitante.getNombre() + " se colocó el equipo. Esperando entorno virtual...");
                
                // Le avisa al servidor que él está listo
                jugadoresListos.release();
                
                // Espera a que el servidor corra el juego y le avise que terminó
                finPartida.acquire();
                
                visores.release(1);
                manoplas.release(2);
                bases.release(1);
                System.out.println("[RV]: " + visitante.getNombre() + " se quitó el equipo y lo devolvió.");
            } else {
                System.out.println("[RV]: " + visitante.getNombre() + " no encontró equipo completo y se fue.");
            }
        } catch (InterruptedException e) {
            System.out.println("[RV]: " + visitante.getNombre() + " fue interrumpido en el entorno virtual.");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            try {
                // El servidor espera hasta que haya al menos 1 jugador listo
                if (jugadoresListos.tryAcquire(2, TimeUnit.SECONDS)) {
                    
                    // Toma todos los demás permisos de los que llegaron en este momento exacto
                    int jugadoresEnEstaPartida = 1 + jugadoresListos.drainPermits();
                    
                    System.out.println("[RV]: === Servidor iniciando renderizado con " + jugadoresEnEstaPartida + " jugadores ===");
                    Thread.sleep(3000); // El servidor hace el viaje
                    System.out.println("[RV]: === Sesión de simulación finalizada. Desconectando jugadores ===");
                    
                    // Libera la cantidad EXACTA de permisos de los jugadores que participaron
                    finPartida.release(jugadoresEnEstaPartida);
                    
                    Thread.sleep(500); // Da tiempo a que devuelvan las cosas
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        finPartida.release(10); // Cierre de emergencia
    }
}