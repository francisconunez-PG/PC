package ParqueAtraccion.Atracciones;

import java.util.concurrent.Exchanger;

import hilos.Visitante;


public class JuegoPremios {

    // El Exchanger intercambia un String (Ficha) por otro String (Premio).
    private final Exchanger<String> intercambiador;

    public JuegoPremios() {
        this.intercambiador = new Exchanger<>();
        // El encargado del juego debe ser un hilo aparte que espera el intercambio.
        new Thread(new EncargadoJuegos(intercambiador)).start();
    }

    public String participar(Visitante visitante, String ficha) {
        try {
            System.out.println("[JUEGOS] " + visitante.getNombre() + " ofrece su " + ficha + " al Encargado.");
            // El visitante espera a que el Encargado le dé el premio a cambio de su ficha.
            String premio = intercambiador.exchange(ficha);
            return premio;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupción. No hubo premio. :(";
        }
    }

    // Hilo interno que simula al empleado esperando la ficha.
    private static class EncargadoJuegos implements Runnable {
        private final Exchanger<String> intercambiador;

        public EncargadoJuegos(Exchanger<String> ex) {
            this.intercambiador = ex;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // El encargado espera a recibir la ficha del visitante.
                    String fichaRecibida = intercambiador.exchange("Premio Grande");
                    System.out.println("[JUEGOS]: Encargado recibió: " + fichaRecibida + ". Entregando premio. :)");
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
