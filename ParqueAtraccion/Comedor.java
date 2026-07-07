package ParqueAtraccion;

import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Comedor {
    private final int cantMesas = 3;
    private final CyclicBarrier[] mesas = new CyclicBarrier[cantMesas]; // Cada mesa tiene una barrera para 4 visitantes.
    private final int[] integrantesMesa = new int[cantMesas]; // Controla cuántos integrantes hay en cada mesa.
    

    private int lugaresOcupados = 0; //Controla la cantidad de lugares ocupados en el comedor.
    private final Parque parque;

    public Comedor(Parque parque) {
        this.parque = parque;
        for (int i = 0; i < cantMesas; i++) {
            final int numeroDeMesa = i;
            mesas[i] = new CyclicBarrier(4, () -> {
                System.out.println("[COMEDOR]: Mesa " + (numeroDeMesa + 1) + " de 4 completa. ¡Empiezan a comer!");
            });
        }
    }

    public void comer(Visitante visitante) {
        boolean pudoSentarse = false;
        int mesaAsignada = -1;
        
        if (parque.estanActividadesAbiertas()) {
            try {
                // Monitor para revisar capacidad y buscar mesa.
                synchronized (this) {
                    if (lugaresOcupados >= 12) {
                        System.out.println("[COMEDOR]: " + visitante.getNombre() + " vio el comedor lleno y prefirió volver luego.");
                        return;
                    }
                    
                    lugaresOcupados++; // Toma un lugar.

                    // Busca què mesa tiene espacio y se anota.
                    for (int i = 0; i < cantMesas; i++) {
                        if (integrantesMesa[i] < 4) {
                            mesaAsignada = i;
                            integrantesMesa[i]++;
                            break;
                        }
                    }
                }

                System.out.println("[COMEDOR]: " + visitante.getNombre() + " se sentó en la Mesa " + (mesaAsignada + 1) + " y espera compañía.");
                
                try {
                    // Barrera de alto nivel.
                    mesas[mesaAsignada].await(5, TimeUnit.SECONDS);
                    pudoSentarse = true;
                } catch (TimeoutException e) {
                    System.out.println("[COMEDOR]: " + visitante.getNombre() + " se cansó de esperar (Timeout).");
                    synchronized (this) { integrantesMesa[mesaAsignada] = 0; }
                    mesas[mesaAsignada].reset();
                } catch (BrokenBarrierException e) {
                    System.out.println("[COMEDOR]: " + visitante.getNombre() + " se fue de la Mesa " + (mesaAsignada + 1) + " porque se desarmó.");
                }
                
            } catch (InterruptedException e) {
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " fue interrumpido antes de comer.");
                Thread.currentThread().interrupt();
            } finally {
                // Si la mesa falló, libera su asiento.
                if (!pudoSentarse) {
                    synchronized (this) {
                        if (mesaAsignada != -1 && integrantesMesa[mesaAsignada] > 0) {
                            integrantesMesa[mesaAsignada]--;
                        }
                        lugaresOcupados--;
                    }
                }
            }
        }

        if (pudoSentarse) {
            try {
                Thread.sleep(2500); // Comiendo.
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " terminó su comida en la Mesa " + (mesaAsignada + 1) + ".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Al terminar, se levanta y libera el lugar bajo el monitor.
                synchronized (this) {
                    integrantesMesa[mesaAsignada]--;
                    lugaresOcupados--;
                }
            }
        }
    }
}