package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class BarcoPirata {

    private static final int capacidad = 20;
    private static final long tiempoEspera = 4000;

    private final Semaphore asientos = new Semaphore(capacidad, true);
    private final Parque parque;
    private int pasajeros = 0;
    private boolean viajeEnCurso = false;

    public BarcoPirata(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();

        try {
            asientos.tryAcquire(); // Intentan conseguir asiento.

            boolean soyElCapitan = false;

            synchronized (this) {
                pasajeros++;
                System.out.println("[BP]: " + nombre + " sube (" + pasajeros + "/" + capacidad + ")");

                long inicio = System.currentTimeMillis();
                long restante = tiempoEspera;

                // Esperan mientras no esté lleno, el barco no haya salido y el parque siga abierto.
                while (pasajeros < capacidad && !viajeEnCurso && parque.estanActividadesAbiertas() && restante > 0) {
                    wait(restante);
                    restante = tiempoEspera - (System.currentTimeMillis() - inicio);
                }

                // Si no arrancó y todavía es horario, este hilo se hace cargo de iniciar el viaje.
                if (!viajeEnCurso && parque.estanActividadesAbiertas()) {
                    viajeEnCurso = true;
                    soyElCapitan = true;
                    notifyAll();
                }
                
                // Los pasajeros normales esperan a que el capitan termine el viaje.
                while (viajeEnCurso && !soyElCapitan) {
                    wait();
                }
            }

            if (soyElCapitan) {
                System.out.println("[BP]: --- Barco Pirata INICIA viaje ---");
                Thread.sleep(3000);
                System.out.println("[BP]: --- Barco Pirata FINALIZA viaje ---");

                synchronized (this) {
                    viajeEnCurso = false;
                    notifyAll(); // Despierta a todos para que se bajen.
                }
            }
            
            // Bajada y salida del barco.
            synchronized (this) {
                pasajeros--;
                System.out.println("[BP]: " + nombre + " baja del barco.");
                
                // Suelta el permiso del semáforo recién cuando baja.
                asientos.release();

                if (pasajeros == 0) {
                    System.out.println("[BP]: Barco vacío. Listos para la siguiente vuelta.");
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


