package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;

public class MontanaRusa {
    // Contadores para saber cuánta gente hay en el carro y cuántos ya se bajaron.
    private int pasajerosAbordo = 0;
    private int pasajerosQueBajaron = 0;
    private int enEspera = 0;
    private boolean enViaje = false;
    
    // Constantes para el tamaño del carro y el largo de la fila.
    private final int capacidad = 5;
    private final int limiteEspera = 10;
    private final Parque parque;

    public MontanaRusa(Parque parque) {
        this.parque = parque;
    }

    public void subir(Visitante visitante) {
        boolean participoDelViaje = false;

        synchronized (this) {
            if (enEspera >= limiteEspera) {
                System.out.println("[MONTAÑA]: " + visitante.getNombre() + " se retiró por fila llena.");
            } else {
                enEspera++;
                boolean salioDeLaFila = false;
                try {
                    // Hacemos que esperen si el carro está lleno o dando la vuelta.
                    while ((pasajerosAbordo >= capacidad || enViaje) && parque.estanActividadesAbiertas()) {
                        wait(2000);
                    }

                    enEspera--;
                    salioDeLaFila = true; // Marcamos que ya avanzó para no restarlo de nuevo por error si hay una interrupción.

                    // Si nos despertamos y resulta que el parque cerró, nos vamos.
                    if (!parque.estanActividadesAbiertas() && (pasajerosAbordo >= capacidad || enViaje)) {
                        System.out.println("[MONTAÑA]: " + visitante.getNombre() + " se fue de la fila porque cerraron.");
                    } else {
                        pasajerosAbordo++;
                        System.out.println("[MONTAÑA]: " + visitante.getNombre() + " subió (" + pasajerosAbordo + "/" + capacidad + ").");

                        if (pasajerosAbordo == capacidad) {
                            // Si soy el quinto pasajero, me toca avisar que arranquemos.
                            System.out.println("[MONTAÑA]: ¡Carro lleno! Iniciando el viaje...");
                            enViaje = true;
                            participoDelViaje = true;
                            notifyAll();
                        } else {
                            // Como todavía faltan lugares, me quedo esperando arriba del carro.
                            while (!enViaje && parque.estanActividadesAbiertas()) {
                                wait(2000);
                            }
                            
                            // Si me despierto y no arrancamos, es porque cerró el parque y el carro nunca se llenó.
                            if (!enViaje) {
                                pasajerosAbordo--;
                                System.out.println("[MONTAÑA]: " + visitante.getNombre() + " se bajó triste. El parque cerró y no se llenó el carro.");
                            } else {
                                participoDelViaje = true;
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    // Si se corta el hilo vemos dónde estaba parado para restar el contador correcto.
                    if (!salioDeLaFila) {
                        enEspera--;
                    } else if (!enViaje) {
                        pasajerosAbordo--;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (participoDelViaje) {
            try {
                // Esto simula el tiempo que dura la vuelta en la montaña rusa.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Volvemos a bloquear para no pisarnos contando.
            synchronized (this) {
                pasajerosQueBajaron++;
                System.out.println("[MONTAÑA]: " + visitante.getNombre() + " terminó el viaje y se bajó.");

                // El último en bajarse se encarga de resetear el carro y avisarle a los de la fila.
                if (pasajerosQueBajaron == capacidad) {
                    pasajerosAbordo = 0;
                    pasajerosQueBajaron = 0;
                    enViaje = false;
                    System.out.println("[MONTAÑA]: El carro está vacío y listo para un nuevo grupo.");
                    notifyAll();
                }
            }
        }
    }
}