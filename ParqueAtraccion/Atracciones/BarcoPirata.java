package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class BarcoPirata implements Runnable {
    // Lock y Condiciones
    private final Lock lock = new ReentrantLock(true); // Respeta el orden de llegada para subir al barco.
    private final Condition esperaFila = lock.newCondition(); // Fila hasta que puedan subir al barco.
    private final Condition esperaViaje = lock.newCondition(); // Visitantes esperando a que termine el viaje.
    private final Condition esperaMaquina = lock.newCondition(); // Para coordinar con el hilo de la máquina.
    
    private int pasajerosAbordo = 0;
    private int pasajerosQueBajaron = 0;
    private boolean enViaje = false;
    private final int capacidad = 20;
    private final Parque parque;

    public BarcoPirata(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        boolean pudoSubir = false;
        boolean completoViaje = false;
        
        lock.lock();
        try {
            long tiempoRestante = TimeUnit.SECONDS.toNanos(3);
            boolean seCanso = false;
            
            // Espera en la fila
            while ((pasajerosAbordo >= capacidad || enViaje) && !seCanso && parque.estanActividadesAbiertas()) {
                if (tiempoRestante <= 0) {
                    seCanso = true;
                } else {
                    tiempoRestante = esperaFila.awaitNanos(tiempoRestante);
                }
            }
            
            
            if (seCanso) {
                System.out.println("[BARCO]: " + visitante.getNombre() + " se cansó de esperar en la fila y se fue.");
            } else if (!parque.estanActividadesAbiertas()) {
                esperaFila.signalAll(); // Despierta a los demás de la fila para que puedan irse al terminar el día.
                } else {
                    // El visitante sube con éxito al barco.
                    pasajerosAbordo++;
                    System.out.println("[BARCO]: " + visitante.getNombre() + " se subió al barco pirata (" + pasajerosAbordo + "/" + capacidad + ")");
                    pudoSubir = true;

                    if (pasajerosAbordo == capacidad) {
                        esperaMaquina.signal(); // Despierta a la máquina si se llenó.
                    }

                    // El pasajero duerme esperando que termine el viaje.
                    while ((enViaje || pasajerosAbordo < capacidad) && parque.estanActividadesAbiertas()) {
                        esperaViaje.await();
                    }
                
                    // Si al salir del bucle de espera el parque sigue abierto, el viaje terminó con éxito
                    if (parque.estanActividadesAbiertas()) {
                        completoViaje = true;
                    }
                }

        } catch (InterruptedException e) {
            System.out.println("[BARCO]: " + visitante.getNombre() + " se fue de la fila por una interrupción.");
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        // Proceso de bajada.
        // Solo se ejecuta si efectivamente subió y logró completar el recorrido sin evacuaciones por cierre.
        if (pudoSubir && completoViaje) {
            lock.lock();
            try {
                pasajerosQueBajaron++;
                System.out.println("[BARCO]: " + visitante.getNombre() + " se bajó del barco.");
                
                if (pasajerosQueBajaron == capacidad) {
                    pasajerosAbordo = 0;
                    pasajerosQueBajaron = 0;
                    enViaje = false;
                    System.out.println("[BARCO]: El barco quedó totalmente vacío y listo para otra vuelta.");
                    esperaMaquina.signal(); // Libera a la máquina para su próximo ciclo
                    esperaFila.signalAll(); // Llama a los de la fila
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            lock.lock();
            try {
                // El barco espera a llenarse.
                while (pasajerosAbordo < capacidad && parque.estanActividadesAbiertas()) {
                    esperaMaquina.await(1, TimeUnit.SECONDS);
                }
                if (parque.estanActividadesAbiertas()){
                    enViaje = true;
                    System.out.println("[BARCO]: El barco pirata se llenó y empieza a hamacarse.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }

            //Simulación del viaje del barco.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock.lock();
            try {
                System.out.println("[BARCO]: El barco se detiene por completo.");
                esperaViaje.signalAll(); // Avisa a los pasajeros que el viaje terminó para que bajen.
                
                // Espera a que se bajen todos los pasajeros.
                while (pasajerosQueBajaron < capacidad && pasajerosAbordo == capacidad && parque.estanActividadesAbiertas()) {
                    esperaMaquina.await(500, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
        
        // Al cierre del parque, se libera cualquier hilo remanente.
        lock.lock();
        try {
            esperaViaje.signalAll();
            esperaFila.signalAll();
        } finally {
            lock.unlock();
        }
    }
}