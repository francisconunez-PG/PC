package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.Semaphore;


public class MontanaRusa {

    // Variables de Estado.
    private static final int capacidadCarro = 5;
    private static final int salaEspera = 10;

    private int visitantesEnCarro; // Personas esperando para el viaje actual
    private boolean viajeEnCurso = false; // Indica si el viaje está en curso.
    
    private final Semaphore semaforoSalaEspera; // Controla el espacio limitado.
    
    public MontanaRusa() {
        this.visitantesEnCarro = 0;
        this.semaforoSalaEspera = new Semaphore(salaEspera);
    }
    
    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();

        try {
            // Intentar entrar a la sala de espera.
            if (semaforoSalaEspera.tryAcquire()) {
                // Usar Monitor para subir al carro.
                
                System.out.println("[MR]: " + nombre + " entra a la sala de espera de la Montaña Rusa. Carro: " + visitantesEnCarro + "/" + capacidadCarro);
                boolean soyElUltimo = false;

                synchronized (this) {
                visitantesEnCarro++;
                System.out.println("[MR]: " + nombre + " sube al carro (" +
                        visitantesEnCarro + "/" + capacidadCarro + ")");

                if (visitantesEnCarro == capacidadCarro) {
                    // Este hilo inicia el viaje.
                    viajeEnCurso = true;
                    soyElUltimo = true;
                    notifyAll(); // despierta a los demás.
                } else {
                    // Espera a que el carro se llene.
                    while (!viajeEnCurso) {
                        wait();
                    }
                }
            }

            // El último ejecuta el viaje.
            if (soyElUltimo) {
                System.out.println("[MR]: carro lleno. ¡INICIA EL VIAJE!");
                Thread.sleep(2000);
                System.out.println("[MR]: VIAJE FINALIZADO.");

                // Reiniciar estado.
                synchronized (this) {
                    visitantesEnCarro = 0;
                    viajeEnCurso = false;
                    notifyAll(); // permite que todos bajen
                }
            } else {
                // Los demás esperan a que termine el viaje
                synchronized (this) {
                    while (viajeEnCurso) {
                        wait();
                    }
                }
            }

            System.out.println("[MR] " + nombre + " baja del carro.");
            }else{
                // Se va si no hay espacio en la sala de espera.
                System.out.println("[MR]: " +nombre + " encuentra la sala de espera llena y decide no subir a la Montaña Rusa.");
                
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Liberar el espacio en la sala de espera al finalizar.
            semaforoSalaEspera.release();
        }
    }
}