package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrenTuristico implements Runnable {
    // La cola bloqueante maneja la fila de espera automáticamente con sus tiempos.
    private final BlockingQueue<Visitante> estacion = new ArrayBlockingQueue<>(10);
    
    // Lista para saber quiénes están efectivamente arriba del tren dando la vuelta.
    private final List<Visitante> pasajerosAbordo = new ArrayList<>();
    private final Parque parque;

    public TrenTuristico(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        boolean pudoSubir = false;
        try {
            // El visitante intenta entrar a la estación. Espera como máximo 4 segundos si está llena.
            pudoSubir = estacion.offer(visitante, 4, TimeUnit.SECONDS);
            
            if (pudoSubir) {
                System.out.println("[TREN]: " + visitante.getNombre() + " entró a la estación y espera el tren.");
                
                // Usamos un monitor clásico para que el visitante espere a que termine su viaje.
                synchronized (this) {
                    // Mientras el visitante siga en la estación o arriba del tren, se queda esperando pacientemente.
                    while ((estacion.contains(visitante) || pasajerosAbordo.contains(visitante)) && parque.estanActividadesAbiertas()) {
                        // Le ponemos un timeout al wait por si se cierra el parque de golpe.
                        wait(2000);
                    }
                }
                
                // Si sale del while, es porque ya no está en la estación ni en el tren.
                if (parque.estanActividadesAbiertas()) {
                    System.out.println("[TREN]: " + visitante.getNombre() + " se bajó del tren al terminar el recorrido.");
                } else {
                    System.out.println("[TREN]: " + visitante.getNombre() + " tuvo que evacuar la estación porque cerró el parque.");
                }
                
            } else {
                System.out.println("[TREN]: " + visitante.getNombre() + " vio la estación llena y se fue a otra atracción.");
            }
        } catch (InterruptedException e) {
            System.out.println("[TREN]: " + visitante.getNombre() + " fue interrumpido mientras esperaba en la estación.");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try {
            // El tren funciona de fondo dando vueltas mientras el parque siga abierto.
            while (parque.estanActividadesAbiertas()) {
                // Simula el tiempo que el tren espera en la estación a que suba la gente.
                Thread.sleep(2000);
                
                synchronized (this) {
                    // El tren abre las puertas y todos los de la fila suben a bordo.
                    // drainTo saca a todos de 'estacion' y los mete en 'pasajerosAbordo' de un solo movimiento.
                    estacion.drainTo(pasajerosAbordo);
                }
                
                if (!pasajerosAbordo.isEmpty()) {
                    System.out.println("[TREN]: ¡Chu chu! El tren arranca con " + pasajerosAbordo.size() + " pasajeros a bordo.");
                    
                    // Simulamos el tiempo del viaje real. Lo hacemos afuera del synchronized para no bloquear a los que llegan a la estación.
                    Thread.sleep(3000);
                    
                    synchronized (this) {
                        // El viaje terminó. Vaciamos el tren para que los visitantes puedan salir de su bucle while.
                        pasajerosAbordo.clear();
                        System.out.println("[TREN]: El tren volvió a la estación y abre las puertas.");
                        
                        // Le avisamos a los visitantes que estaban durmiendo que revisen su estado.
                        notifyAll();
                    }
                } else {
                    System.out.println("[TREN]: El tren llegó a la estación pero no había pasajeros esperando. Esperando un poco más...");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[TREN]: El chofer del tren recibe el aviso de cierre y guarda la locomotora.");
            Thread.currentThread().interrupt();
        }
    }
}