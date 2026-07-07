package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;

public class MontanaRusa implements Runnable {
    private int pasajerosAbordo = 0;
    private int pasajerosQueBajaron = 0;
    private boolean enViaje = false;
    
    private final int capacidad = 5;
    private final Parque parque;

    public MontanaRusa(Parque parque) {
        this.parque = parque;
    }

    public synchronized void subir(Visitante visitante) {
        try {
            // El visitante espera en la fila si el carro está lleno o dando la vuelta
            while ((pasajerosAbordo >= capacidad || enViaje) && parque.estanActividadesAbiertas()) {
                wait(2000);
            }

            if (!parque.estanActividadesAbiertas()) return;

            pasajerosAbordo++;
            System.out.println("[MONTAÑA]: " + visitante.getNombre() + " se subió al carro (" + pasajerosAbordo + "/" + capacidad + ").");

            if (pasajerosAbordo == capacidad) {
                notifyAll(); // Le avisa al hilo de la máquina que el carro se llenó y puede arrancar.
            }

            // El visitante se queda "dormido" durante el viaje.
            while (enViaje || pasajerosAbordo < capacidad) {
                if (!parque.estanActividadesAbiertas()) return;
                wait();
            }

            // Termina el viaje y se bajan de a uno
            pasajerosQueBajaron++;
            System.out.println("[MONTAÑA]: " + visitante.getNombre() + " terminó el viaje y se bajó.");

            // El último en bajarse se encarga de resetear el carro y avisarle a los de la fila.
            if (pasajerosQueBajaron == capacidad) {
                pasajerosAbordo = 0;
                pasajerosQueBajaron = 0;
                enViaje = false;
                System.out.println("[MONTAÑA]: El carro está vacío y listo para un nuevo grupo.");
                notifyAll(); // Despierta a los de la fila
            }

        } catch (InterruptedException e) {
            System.out.println("[MONTAÑA]: " + visitante.getNombre() + " se tuvo que ir por una interrupción.");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            synchronized (this) {
                try {
                    // Espera hasta que se suban los 5 visitantes.
                    while (pasajerosAbordo < capacidad && parque.estanActividadesAbiertas()) {
                        wait(1000);
                    }
                    if (!parque.estanActividadesAbiertas()) break;

                    enViaje = true;
                    System.out.println("[MONTAÑA]: ¡El tren de la montaña rusa arranca!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Simula el tiempo de la vuelta.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            synchronized (this) {
                System.out.println("[MONTAÑA]: El viaje terminó. El tren frena.");
                notifyAll(); // Despierta a los 5 pasajeros para que se bajen.
                
                // Espera a que los 5 se bajen antes de abrir de nuevo las puertas.
                while (pasajerosQueBajaron < capacidad && pasajerosAbordo == capacidad && parque.estanActividadesAbiertas()) {
                    try { wait(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
        }
        synchronized (this) { notifyAll(); } // Libera si cierra el parque.
    }
}