package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class BarcoPirata {
    
    // Usamos el cerrojo en modo justo para respetar el orden de la fila.
    private final Lock lock = new ReentrantLock(true);
    private final Condition espera = lock.newCondition();
    
    // Llevamos la cuenta de la gente y el estado del barco.
    private int pasajerosAbordo = 0;
    private boolean enViaje = false;
    private final int CAPACIDAD = 20;

    public void subir(Visitante visitante) {
        boolean pudoSubir = false;
        
        // Pedimos el cerrojo antes de tocar cualquier variable compartida.
        lock.lock();
        try {
            // Guardamos el tiempo máximo de espera convertido a nanosegundos para el while.
            long tiempoRestante = TimeUnit.SECONDS.toNanos(3);
            boolean seCanso = false;
            
            // Usamos while para evitar que un despertar falso suba a alguien al barco lleno.
            // Le agregamos la condición de que no se haya cansado de esperar.
            while ((pasajerosAbordo >= CAPACIDAD || enViaje) && !seCanso) {
                if (tiempoRestante <= 0) {
                    seCanso = true;
                } else {
                    // El awaitNanos frena el hilo y nos devuelve el tiempo que le sobró de esos 3 segundos.
                    tiempoRestante = espera.awaitNanos(tiempoRestante);
                }
            }
            
            // Si salimos del while y no se cansó, es porque hay lugar en el barco.
            if (seCanso) {
                System.out.println("[BARCO]: " + visitante.getNombre() + " se cansó de esperar lugar y se fue.");
            } else {
                pasajerosAbordo++;
                pudoSubir = true;
                System.out.println("[BARCO]: " + visitante.getNombre() + " subió al barco (" + pasajerosAbordo + "/" + CAPACIDAD + ").");
                
                // El visitante que llena el barco es el encargado de avisar que arranca.
                if (pasajerosAbordo == CAPACIDAD) {
                    enViaje = true;
                    System.out.println("[BARCO]: El barco pirata se llenó y empieza a hamacarse.");
                }
            }
            
        } catch (InterruptedException e) {
            System.out.println("[BARCO]: " + visitante.getNombre() + " se fue de la fila por una interrupción.");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        if (pudoSubir) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("[BARCO]: A " + visitante.getNombre() + " le cortaron el viaje por la mitad.");
                Thread.currentThread().interrupt();
            }
            
            // Volvemos a pedir el cerrojo para bajarnos de a uno sin pisar los números.
            lock.lock();
            try {
                pasajerosAbordo--;
                System.out.println("[BARCO]: " + visitante.getNombre() + " se bajó del barco pirata.");
                
                // El último que pone un pie fuera del barco resetea todo y avisa a los de la fila.
                if (pasajerosAbordo == 0) {
                    enViaje = false;
                    System.out.println("[BARCO]: El barco quedó totalmente vacío y listo para otra vuelta.");
                    espera.signalAll();
                }
            } finally {
                
                lock.unlock();
            }
        }
    }
}