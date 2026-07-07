package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrenTuristico implements Runnable {
    //
    private final BlockingQueue<Visitante> estacion = new ArrayBlockingQueue<>(10); // Capacidad de la estación para esperar visitantes.
    private final List<Visitante> pasajerosAbordo = new ArrayList<>(); // Lista de pasajeros actualmente en el tren.
    private final Parque parque;

    public TrenTuristico(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        boolean pudoSubir = false;
        try {
            // El visitante entra a la estación.
            pudoSubir = estacion.offer(visitante, 4, TimeUnit.SECONDS);
            
            if (pudoSubir) {
                System.out.println("[TREN]: " + visitante.getNombre() + " entró a la estación y espera el tren.");
                
                // Monitor para esperar a que termine su recorrido.
                synchronized (this) {
                    while ((estacion.contains(visitante) || pasajerosAbordo.contains(visitante)) && parque.estanActividadesAbiertas()) {
                        wait(2000); // Espera a que el tren vuelva a la estación para bajarse.
                    }
                }
                System.out.println("[TREN]: " + visitante.getNombre() + " se bajó del tren en la estación de destino.");
            } else {
                System.out.println("[TREN]: " + visitante.getNombre() + " vio la estación llena y prefirió no esperar.");
            }
        } catch (InterruptedException e) {
            System.out.println("[TREN]: A " + visitante.getNombre() + " le interrumpieron la espera en la estación.");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try {
            while (parque.estanActividadesAbiertas()) {
                Thread.sleep(1000); // Frenado en estación.
                
                synchronized (this) {
                    // Pasa todos los que estaban en la cola de bloqueo(de espera) directamente al tren (lista).
                    estacion.drainTo(pasajerosAbordo);
                }
                
                if (!pasajerosAbordo.isEmpty()) {
                    System.out.println("[TREN]: ¡Chu chu! El tren arranca con " + pasajerosAbordo.size() + " pasajeros a bordo.");
                    
                    Thread.sleep(3000); // Simulando el viaje.
                    
                    synchronized (this) {
                        pasajerosAbordo.clear();
                        System.out.println("[TREN]: El tren volvió a la estación y abre las puertas.");
                        notifyAll(); // Despierta a los pasajeros para bajarse.
                    }
                } else {
                    System.out.println("[TREN]: Esperando pasajeros en la estación...");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[TREN]: El chofer guarda la locomotora.");
            Thread.currentThread().interrupt();
        }
    }
}