package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Semaphore;

public class BarcoPirata {

    private static final int capacidad = 20;
    private static final long tiempoEspera = 4000;

    private final Semaphore asientos = new Semaphore(capacidad, true);
    private final Parque parque; // Quitamos 'static' si no es necesario
    private int pasajeros = 0;
    
    private boolean viajeEnCurso = false;

    public BarcoPirata(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();

        try {
            asientos.acquire(); // 1. Agarran ticket

            boolean soyElCapitan = false; // Variable local para identificar al que arranca

            synchronized (this) {
                pasajeros++;
                System.out.println("[BP]: " + nombre + " sube (" + pasajeros + "/" + capacidad + ")");

                // Lógica de llenado o Timeout
                long inicio = System.currentTimeMillis();
                long restante = tiempoEspera;

                // Esperamos si NO estamos llenos Y NO hay viaje en curso Y el parque sigue abierto
                while (pasajeros < capacidad && !viajeEnCurso && parque.estanActividadesAbiertas() && restante > 0) {
                    
                    restante = tiempoEspera - (System.currentTimeMillis() - inicio);
                    
                    if (restante <= 0) {
                        System.out.println("[BP]: Tiempo de espera agotado. Salimos con: " + pasajeros);
                        
                    }else{
                        wait(restante);
                    }
                    // Al despertar, los hilos chequean si el parque cerró.
                    if (!parque.estanActividadesAbiertas() && !viajeEnCurso) {
                        System.out.println("[BP]: " + nombre + " se baja porque el parque cerró.");
                        pasajeros--;
                        asientos.release();
                        notifyAll(); // Avisamos a otros para que no esperen el timeout.
                        return; // Salimos del método porque el parque cerró. <--- preguntar si se puede hacer esto o si es mejor usar un flag para que el hilo sepa que no debe seguir esperando.
                    }
                }

                // Verificamos si nos toca iniciar el viaje
                if (!viajeEnCurso && pasajeros > 0) {
                    viajeEnCurso = true;
                    soyElCapitan = true; // hilo que inicia el viaje como "capitan".
                    notifyAll();
                }

                
                // El capitán, NO espera. tripulaciòn, espera.
                while (viajeEnCurso && !soyElCapitan) {
                    wait();
                }
                
                
            }

            
            if (soyElCapitan) { // Solo el capitán simula el viaje. Los demás esperan a que termine.
                System.out.println("[BP]: --- Barco Pirata INICIA viaje ---");
                Thread.sleep(3000);
                System.out.println("[BP]: --- Barco Pirata FINALIZA viaje ---");

                synchronized (this) {
                    viajeEnCurso = false;
                    notifyAll(); // Despierta a los pasajeros para que bajen
                }
            }
            
            // Todos los pasajeros, incluido el capitán, bajan después del viaje.
            synchronized (this) {
                asientos.release();
                pasajeros--;
                System.out.println("[BP]: " + nombre + " baja del barco.");

                // El último libera los tickets para el siguiente grupo.
                if (pasajeros == 0) {
                    System.out.println("[BP]: Barco vacío. Tickets liberados.");
                    
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


