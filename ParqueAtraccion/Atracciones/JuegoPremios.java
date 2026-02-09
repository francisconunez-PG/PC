package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

public class JuegoPremios {

    // El Exchanger intercambia un String (Ficha) por otro String (Premio).
    private final Exchanger<String> intercambiador;

    // Semáforo para asegurar que sea Visitante vs Encargado, y no Visitante vs Visitante.
    private final Semaphore mutex;

    public JuegoPremios() {
        this.intercambiador = new Exchanger<>();
        this.mutex = new Semaphore(1, true);

        // El encargado del juego debe ser un hilo aparte que espera el intercambio. <-- Crear otra clase o así esta bien?.
        Thread tEncargado = new Thread(new EncargadoJuegos(intercambiador));
        tEncargado.setDaemon(true); // Para que el hilo muera si se cierra el programa.
        tEncargado.start();
    }

    public String participar(Visitante visitante, String ficha) {
        String premio = "";
        try {
            // Bloqueamos para que nadie más interrumpa el intercambio.
            mutex.acquire();

            System.out.println("[JUEGO]: " + visitante.getNombre() + " ofrece su " + ficha + " al Encargado.");
            
            // El visitante espera a que el Encargado le dé el premio.
            premio = intercambiador.exchange(ficha);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            premio = "[JUEGO]: Interrupción. No hubo premio. :,(";
        } finally {
            
            mutex.release();
        }
        return premio;
    }

    // Hilo interno que simula al empleado.
    private static class EncargadoJuegos implements Runnable {
        private final Exchanger<String> intercambiador;

        public EncargadoJuegos(Exchanger<String> hilo) {
            this.intercambiador = hilo;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // El encargado espera recibir ficha y entrega premio.
                    // Se bloqueará aquí hasta que llegue un visitante con el mutex.
                    String fichaRecibida = intercambiador.exchange("Osito de Peluche, Muñeco de Acción, Globo");
                    
                    System.out.println("[JUEGO]: Recibí " + fichaRecibida + ". Entregué premio. :D");
                    
                    // Simula tiempo de acomodar la ficha.
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
