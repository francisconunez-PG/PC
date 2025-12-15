package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrenTuristico implements Runnable {

    private static final int capacidadTren = 10;
    private static final int tiempoEspera = 5; // minutos

    private final BlockingQueue<Visitante> colaPasajeros;

    public TrenTuristico() {
        this.colaPasajeros = new ArrayBlockingQueue<>(capacidadTren);
    }

    public void subirAlTren(Visitante visitante) {
        String nombre = visitante.getNombre();

        if (colaPasajeros.offer(visitante)) {
            System.out.println("[TREN]: " + nombre + " se sube al tren (" +
                    colaPasajeros.size() + "/" + capacidadTren + ")");
        } else {
            System.out.println("[TREN]: " + nombre + " encuentra el tren lleno y se va.");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {

                System.out.println("\n[TREN]: Tren esperando al primer pasajero...");

                // Espera al menos un pasajero
                Visitante primero = colaPasajeros.take();

                List<Visitante> pasajeros = new ArrayList<>();
                pasajeros.add(primero);

                long inicio = System.currentTimeMillis();

                while (pasajeros.size() < capacidadTren) {
                    long restante = TimeUnit.MINUTES.toMillis(tiempoEspera)
                            - (System.currentTimeMillis() - inicio);

                    if (restante <= 0) break;

                    Visitante siguiente = colaPasajeros.poll(restante, TimeUnit.MILLISECONDS);
                    if (siguiente == null) break;

                    pasajeros.add(siguiente);
                }

                System.out.println("[TREN]: Tren parte con " + pasajeros.size() + " pasajeros.");
                Thread.sleep(2000);
                System.out.println("[TREN]: Tren regresa a la estación.");

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

