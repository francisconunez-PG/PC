package ParqueAtraccion.Atracciones;

import java.util.concurrent.Semaphore;

import hilos.Visitante;

public class BarcoPirata {
    
    // Variables de Estado.
    private static final int capacidadBarco = 20;
    private final Semaphore semaforoAsientos; // Controla los 20 asientos disponibles.
    private int pasajerosActuales; // Contador de pasajeros a bordo.
    private boolean viajeEnCurso; // Indica si el barco está viajando.

    public BarcoPirata() {
        this.semaforoAsientos = new Semaphore(capacidadBarco);
        this.pasajerosActuales = 0;
        this.viajeEnCurso = false;
    }

    public synchronized void subir(Visitante visitante) {
        String nombre = visitante.getNombre();
        // Bandera para rastrear si el hilo adquirió el asiento y necesita liberarlo individualmente.
        boolean liberarAsientoIndividualmente = false;

        try {
            // Intenta adquirir el permiso del Semáforo.
            if (semaforoAsientos.tryAcquire()) {
                
                liberarAsientoIndividualmente = true; // Asumo que lo libero, a menos que inicie el viaje.
                
                System.out.println("[BP]: " + nombre + " sube :). Cantidad de pasajeros: " + (pasajerosActuales + 1) + "/" + capacidadBarco);
                pasajerosActuales++;

                if (pasajerosActuales == capacidadBarco) {
                    // barco lleno: iniciar viaje.
                    iniciarViaje();
                    liberarAsientoIndividualmente = false;
                } else {
                    // Esperar a que se llene o timeout.
                    System.out.println("[BP]: " + nombre + " espera 4s.");
                    wait(4000); // El hilo espera, liberando el Monitor (candado 'synchronized').

                    // Al despertar (por timeout o notify).
                    if (pasajerosActuales > 0 && !viajeEnCurso) {
                        // Inicia por timeout con los que haya.
                        iniciarViaje();
                        liberarAsientoIndividualmente = false;
                    }
                }

                // Espera a que el viaje termine.
                while (viajeEnCurso) {
                    wait();
                }

            } else {
                System.out.println("[BP]: " + nombre + " encontró el Barco lleno. Se va. :(");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
                        
        } finally {
            // Si el asiento fue adquirido y el viaje NO inició, o el hilo fue interrumpido,
            // se libera individualmente.
            if (liberarAsientoIndividualmente) {
                System.out.println("[BP] " + nombre + " libera su asiento individualmente y se retira.");
                semaforoAsientos.release();
                pasajerosActuales--;
            }
        }
    }

    /*
    * Inicia el viaje, lo simula y libera a todos los pasajeros y asientos.
    */
    private void iniciarViaje() throws InterruptedException {
        
        if (!viajeEnCurso){
        
            viajeEnCurso = true;
            System.out.println("[BP]: ¡BARCO PIRATA INICIA VIAJE con " + pasajerosActuales + " personas!");
        
            Thread.sleep(3000);
        
            System.out.println("[BP]: Barco Pirata FINALIZA VIAJE.");

            // Liberación de todos los permisos y notificación a todos los hilos.
            semaforoAsientos.release(pasajerosActuales);
            pasajerosActuales = 0;
            viajeEnCurso = false;

            notifyAll(); // Despierta a los hilos que esperaban el fin del viaje.
    
        }
    }
}