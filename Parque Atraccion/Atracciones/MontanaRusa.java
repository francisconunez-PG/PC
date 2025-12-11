package Atracciones;

import java.util.concurrent.Semaphore;

public class MontanaRusa {

    // Variables de Estado.
    private static final int capacidadCarro = 5; // Requiere 5 personas para iniciar.
    private static final int salaEspera = 10; // Capacidad de espera limitada.

    private int visitantesEnCarro; // Personas esperando para el viaje actual
    private final Semaphore semaforoSalaEspera; // Controla el espacio limitado antes de subir.

    public MontanaRusa() {
        this.visitantesEnCarro = 0;
        // Inicializamos el semáforo para la sala de espera
        this.semaforoSalaEspera = new Semaphore(salaEspera);
    }

    /**
     * Permite a un visitante intentar subir a la montaña rusa.
     * Si la sala de espera está llena, se va. Si el carro no está lleno espera.
     */
    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();

        try {
            // Intentar entrar a la sala de espera.
            if (semaforoSalaEspera.tryAcquire()) {
                // Usar Monitor (synchronized) para subir al carro.
                synchronized (this) {
                    System.out.println(nombre + " entra a la sala de espera de la Montaña Rusa. Carro: " + visitantesEnCarro + "/" + capacidadCarro);
                
                    // Añadir al carro y esperar a los demas.
                    visitantesEnCarro++;

                    if (visitantesEnCarro < capacidadCarro) {
                        // Si el carro no está lleno, espera
                        System.out.println(nombre + " espera a que el carro de la Montaña Rusa se llene.");
                        wait(); // Se duerme hasta que lo despierten.
                    }else {
                        // El último visitante llego.
                        System.out.println("¡CARRO LLENO! (" + capacidadCarro + " personas). La Montaña Rusa ¡INICIA SU VIAJE! ");
                    
                        // Notificar a todos los que esperan para iniciar el viaje.
                        notifyAll();
                    
                        // Simular el viaje.
                        Thread.sleep(2000); // 2 segundos de viaje.
                    
                        // Reiniciar el carro.
                        visitantesEnCarro = 0;
                        System.out.println("Montaña Rusa FINALIZA VIAJE. ");
                    }
                }
            }else{
                // Se va si no hay espacio en la sala de espera.
                System.out.println(nombre + " encuentra la sala de espera llena y decide no subir a la Montaña Rusa.");
                
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Liberar el espacio en la sala de espera al finalizar.
            semaforoSalaEspera.release();
        }
    }
}