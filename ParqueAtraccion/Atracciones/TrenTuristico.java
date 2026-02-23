package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;

public class TrenTuristico implements Runnable {

    private final Parque parque;
    private final int capacidadTren = 15;
    private int pasajerosActuales = 0;
    private boolean viajeEnCurso = false;

    public TrenTuristico(Parque parque) {
        this.parque = parque;
    }

    public synchronized void subir(Visitante visitante) {
        String nombre = visitante.getNombre();
        try {
            // Si está lleno o paseando, toca esperar.
            while ((pasajerosActuales >= capacidadTren || viajeEnCurso) && parque.estanActividadesAbiertas()) {
                wait(2000);
            }

            if (!parque.estanActividadesAbiertas()) {
                System.out.println("[TREN]: " + nombre + " vio que el tren cerro y se fue.");
            } else {
                pasajerosActuales++;
                System.out.println("[TREN]: " + nombre + " subio al tren (" + pasajerosActuales + "/" + capacidadTren + ")");
                
                // Le aviso al chofer que hay gente.
                notifyAll();

                boolean esperandoViaje = true;
                
                while (esperandoViaje) {
                    // Si el parque cierra antes de que el tren arranque, se bajan todos.
                    if (!viajeEnCurso && !parque.estanActividadesAbiertas()) {
                        pasajerosActuales--;
                        System.out.println("[TREN]: " + nombre + " se baja porque cerraron antes de arrancar.");
                        esperandoViaje = false;
                    } else if (!viajeEnCurso && pasajerosActuales == 0) {
                        // El viaje terminó (el chofer pone los pasajeros en 0).
                        System.out.println("[TREN]: " + nombre + " se bajo del tren.");
                        esperandoViaje = false;
                    } else {
                        wait(2000);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        System.out.println("[CHOFER]: Tren listo para arrancar la jornada.");
        try {
            while (parque.estanActividadesAbiertas()) {
                synchronized (this) {
                    // Espera a que se suba alguien para arrancar.
                    while (pasajerosActuales == 0 && parque.estanActividadesAbiertas()) {
                        wait(2000);
                    }

                    if (pasajerosActuales > 0 && parque.estanActividadesAbiertas()) {
                        viajeEnCurso = true;
                        System.out.println("[CHOFER]: Arrancando el tren con " + pasajerosActuales + " pasajeros.");
                    }
                }

                if (viajeEnCurso) {
                    Thread.sleep(4000); // Simulamos el recorrido del tren.
                    
                    synchronized (this) {
                        System.out.println("[CHOFER]: Fin del recorrido. Todos abajo.");
                        viajeEnCurso = false;
                        pasajerosActuales = 0;
                        
                        // Aviso para que se bajen los que estaban arriba.
                        notifyAll();
                    }
                }
            }
            System.out.println("[CHOFER]: Termino mi turno. Tren a la cochera.");
        } catch (InterruptedException e) {
            System.out.println("[CHOFER]: Interrumpido. Me voy a casa.");
            Thread.currentThread().interrupt();
        }
    }
}