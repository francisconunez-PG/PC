package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrenTuristico implements Runnable {

    private static final int capacidadTren = 10;
    private final BlockingQueue<Visitante> colaPasajeros;

    public TrenTuristico(Parque parque) {
        this.colaPasajeros = new ArrayBlockingQueue<>(capacidadTren * 2 , true);
        //preguntar si es mejor crear un hilo tipo chofer.
        new Thread(this, "Chofer-Tren").start();
    }

    

    public void subirAlTren(Visitante visitante) {
        String nombre = visitante.getNombre();
        try {
            System.out.println("[TREN]: " + nombre + " llega al andén.");

            // Se Pone en la cola.
            // Usamos put() en vez de offer() para que si el andén está lleno, espere un poco
            // en lugar de irse inmediatamente.

            colaPasajeros.put(visitante);
            
            System.out.println("[TREN]: " + nombre + " ya está en la fila. Esperando salir...");

            
            // El visitante se duerme aquí hasta que el Chofer le avise que el viaje terminó.
            synchronized (visitante) {
                visitante.wait();
            }

            // Despierta cuando el Chofer hace notify().
            System.out.println("[TREN]: " + nombre + " baja del tren feliz.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {

                System.out.println("[TREN]: Tren esperando al primer pasajero...");
                
                // Espera al menos un pasajero.
                Visitante primero = colaPasajeros.take();
                List<Visitante> pasajeros = new ArrayList<>();
                pasajeros.add(primero); // Pasajero se sube al tren.


                long inicio = System.currentTimeMillis();
                long espera = 5000; // 5 segundos
                long restante = espera;
                while (pasajeros.size() < capacidadTren && restante > 0) { //fijarme bien como funca
                    restante = espera - (System.currentTimeMillis() - inicio);

                    if (restante > 0){

                        Visitante siguiente = colaPasajeros.poll(restante, TimeUnit.MILLISECONDS);
                        if (siguiente != null) pasajeros.add(siguiente);
                        
                    }
                }

                System.out.println("[TREN]: Tren parte con " + pasajeros.size() + " pasajeros.");
                Thread.sleep(2000);
                System.out.println("[TREN]: Tren regresa a la estación.");
                // Avisar a todos los pasajeros que el viaje terminó.
                for (int i = 0; i < pasajeros.size(); i++) {
                    Visitante v = pasajeros.get(i);
                    synchronized (v) {
                    v.notify();
                    }
                }
                pasajeros.clear(); // Limpia la lista para el próximo viaje.
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}