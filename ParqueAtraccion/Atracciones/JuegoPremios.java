package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;

public class JuegoPremios {

    private final Parque parque;
    private int visitantesEsperando = 0;
    private boolean puestoOcupado = false;

    public JuegoPremios(Parque parque) {
        this.parque = parque;
    }

    // Método que ejecutan los visitantes
    public synchronized void jugar(Visitante visitante) {
        String nombre = visitante.getNombre();
        try {
            if (parque.estanActividadesAbiertas()) {
                System.out.println("[PREMIOS]: " + nombre + " hace fila para jugar a los dardos.");
                visitantesEsperando++;
                notifyAll(); // Despierta al encargado si estaba durmiendo

                boolean esperandoTurno = true;

                while (esperandoTurno) {
                    // Si el parque cierra mientras hacía fila, se va.
                    if (!parque.estanActividadesAbiertas()) {
                        visitantesEsperando--;
                        System.out.println("[PREMIOS]: " + nombre + " se fue de la fila porque cerraron el puesto.");
                        esperandoTurno = false;
                    } 
                    // Si el puesto se libera, le toca jugar.
                    else if (!puestoOcupado) {
                        puestoOcupado = true;
                        visitantesEsperando--;
                        esperandoTurno = false;
                    } 
                    // Si está ocupado, espera 2 segundos y vuelve a chequear.
                    else {
                        wait(2000);
                    }
                }

                // Juega solo si el puesto quedó ocupado por él (es decir, no se fue por cierre)
                if (puestoOcupado && parque.estanActividadesAbiertas()) {
                    System.out.println("[PREMIOS]: " + nombre + " está tirando los dardos...");
                    Thread.sleep(2000); // Tiempo que tarda en jugar
                    System.out.println("[PREMIOS]: " + nombre + " terminó de jugar y se lleva un peluche.");
                    
                    puestoOcupado = false;
                    notifyAll(); // Le avisa al próximo en la fila
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Método que ejecuta el hilo Encargado (Daemon)
    public void atender() {
        System.out.println("[ENCARGADO PREMIOS]: Listo para repartir peluches.");
        try {
            while (parque.estanActividadesAbiertas()) {
                synchronized (this) {
                    // Si no hay nadie, el encargado espera 2 segundos y revisa la hora.
                    while (visitantesEsperando == 0 && parque.estanActividadesAbiertas()) {
                        wait(2000);
                    }

                    if (visitantesEsperando > 0 && parque.estanActividadesAbiertas()) {
                        // El encargado simplemente observa cómo juegan (el tiempo lo maneja el visitante).
                        notifyAll();
                    }
                }
                Thread.sleep(500); // Pequeña pausa del encargado.
            }
            System.out.println("[ENCARGADO PREMIOS]: Cierro el puesto y me voy.");
        } catch (InterruptedException e) {
            System.out.println("[ENCARGADO PREMIOS]: Interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}