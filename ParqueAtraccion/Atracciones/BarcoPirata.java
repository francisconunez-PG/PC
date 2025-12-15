package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class BarcoPirata {

    private static final int capacidad = 20;
    private static final long tiempoEspera = 4000; // ms

    private final Semaphore asientos = new Semaphore(capacidad, true);

    private int pasajeros = 0;
    private boolean viajeEnCurso = false;

    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();

        try {
            // Espera hasta conseguir asiento.
            asientos.acquire();

            boolean iniciaViaje = false;

            synchronized (this) {
                pasajeros++;
                System.out.println("[BP]: " + nombre + " sube (" +
                        pasajeros + "/" + capacidad + ")");

                if (pasajeros == capacidad) {
                    viajeEnCurso = true;
                    iniciaViaje = true;
                    notifyAll();
                } else {
                    long inicio = System.currentTimeMillis();
                    while (!viajeEnCurso) {
                        long restante = tiempoEspera -
                                (System.currentTimeMillis() - inicio);
                        if (restante <= 0) break;
                        wait(restante);
                    }

                    if (!viajeEnCurso) {
                        viajeEnCurso = true;
                        iniciaViaje = true;
                        notifyAll();
                    }
                }

                // Espera a que termine el viaje.
                while (viajeEnCurso) {
                    wait();
                }
            }

            // Simulación del viaje.
            if (iniciaViaje) {
                int pasajerosViaje;

                synchronized (this) {
                    pasajerosViaje = pasajeros;
                }

                System.out.println("[BP]: Barco Pirata INICIA viaje con "
                        + pasajerosViaje + " pasajeros");
                Thread.sleep(3000);
                System.out.println("[BP]: Barco Pirata FINALIZA viaje");

                synchronized (this) {
                    pasajeros = 0;
                    viajeEnCurso = false;
                    asientos.release(pasajerosViaje);
                    notifyAll();
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


