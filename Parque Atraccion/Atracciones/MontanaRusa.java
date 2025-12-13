package Atracciones;

import java.util.concurrent.Semaphore;

public class MontanaRusa {

    // Variables de Estado.
    private static final int capacidadCarro = 5;
    private static final int salaEspera = 10;

    private int visitantesEnCarro; // Personas esperando para el viaje actual
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
                
                System.out.println(nombre + " entra a la sala de espera de la Montaña Rusa. Carro: " + visitantesEnCarro + "/" + capacidadCarro);
                synchronized (this) {
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