package Main;

import ParqueAtraccion.Parque;
import hilos.Visitante;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ABRIENDO EL PARQUE ===");

        Parque parque = new Parque();
        Thread hiloParque = new Thread(parque, "Reloj-Parque");
        hiloParque.start();

        // Arrancamos el tren como Daemon para que no bloquee el cierre a la noche.
        Thread hiloTren = new Thread(parque.getTrenTuristico(), "Chofer-Tren");
        hiloTren.setDaemon(true);
        hiloTren.start();

        // Arrancamos al encargado de los premios también como Daemon.
        Thread hiloPremios = new Thread(() -> parque.getJuegoPremios().atender(), "Encargado-Premios");
        hiloPremios.setDaemon(true);
        hiloPremios.start();

        // Hilo que genera visitantes constantemente hasta las 18:00.
        Thread generadorVisitantes = new Thread(() -> {
            int contador = 1;
            try {
                while (parque.isIngresoAbierto()) {
                    Visitante v = new Visitante("Visitante-" + contador, parque);
                    new Thread(v).start();
                    contador++;
                    
                    // Tiempo entre que llega un visitante y el otro.
                    Thread.sleep(1500);
                }
                System.out.println("--- GENERADOR: Ya son las 18:00, no entra más nadie. ---");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        generadorVisitantes.start();

        try {
            // El Main se queda esperando a que el reloj marque las 23:00 y el hilo del parque termine.
            hiloParque.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("=== SIMULACIÓN FINALIZADA. PARQUE CERRADO ===");
        System.out.println("Número de hilos activos: " + Thread.activeCount());
    }
}