package ParqueAtraccion.Atracciones;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import hilos.Visitante;


public class TrenTuristico implements Runnable {
    // Variables de Estado.
    private static final int capacidadTren = 10;
    private static final int TiempoDeEspera = 5;
    private final BlockingQueue<Visitante> colaPasajeros;
    

    public TrenTuristico() {
        this.colaPasajeros = new ArrayBlockingQueue<>(capacidadTren);
    }

    public boolean subirAlTren(Visitante visitante) throws InterruptedException {
        String nombre = visitante.getNombre();
        System.out.println("[TREN]: " + nombre + " se coloca en la cola. (Actual: " + colaPasajeros.size() + "/" + capacidadTren + ")");
        
        // Intenta subir, esperando el tiempo límite si está llena la cola.
        boolean aceptado = colaPasajeros.offer(visitante, TiempoDeEspera, TimeUnit.MINUTES);
        
        if (!aceptado) {
            System.out.println("[TREN]: " + nombre + " se cansó de esperar el tren y se fue.");
        }
        return aceptado;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("\n[TREN]: Tren esperando pasajeros.");
                
                // Espera al primer pasajero indefinidamente.
                Visitante primerPasajero = colaPasajeros.take();
                
                // Vuelve a poner al pasajero para iniciar la cuenta.
                colaPasajeros.put(primerPasajero);
                
                int pasajerosActuales = colaPasajeros.size();

                if (pasajerosActuales < capacidadTren) {
                    System.out.println("[TREN]: Esperando a llenarse o 5 minutos para irse...");
                    // Espera simulada, si se llena antes, el tren no espera (ya que take/put ya hicieron su trabajo).
                    Thread.sleep(TimeUnit.MINUTES.toMillis(TiempoDeEspera * 100));
                }

                // Recolecta a todos los pasajeros que hayan subido.
                List<Visitante> pasajerosAbordo = new ArrayList<>();
                int cantidadAbordo = colaPasajeros.drainTo(pasajerosAbordo);
                
                if (cantidadAbordo > 0) {
                    System.out.println("[TREN]: TREN partiendo con " + cantidadAbordo + " pasajeros. ");
                    Thread.sleep(2000);
                    System.out.println("[TREN]: TREN llegando a la estación.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
