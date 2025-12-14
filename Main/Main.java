package Main;

import hilos.Visitante;
import ParqueAtraccion.Parque;

// Método Main para ejecutar la simulación.
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;
public class Main {
    public static void main(String[] args) {
        
        Parque parque = new Parque();

        // Iniciar el gestor de horarios del Parque.
        Thread hiloParque = new Thread(parque);
        hiloParque.start();

        // Iniciar el hilo del Tren (que espera y parte).
        Thread hiloTren = new Thread(parque.getTrenTuristico());
        hiloTren.setDaemon(true); // Se detiene cuando el programa principal termina.
        hiloTren.start();

        // Generaremos 30 visitantes durante las 9 horas de ingreso (9:00 a 18:00)
        ScheduledExecutorService generadorVisitantes = Executors.newSingleThreadScheduledExecutor();
        
        // La tarea de generar visitantes se repite cada X tiempo mientras el ingreso esté abierto.
        generadorVisitantes.scheduleAtFixedRate(new Runnable() {
            private int contador = 1;
            public void run() {
                if (parque.isParqueAbierto()) {
                    Visitante nuevoVisitante = new Visitante("Visitante-" + contador++, parque);
                    new Thread(nuevoVisitante).start();
                } else {
                    // Si el ingreso cerró, detener la generación de visitantes.
                    generadorVisitantes.shutdown();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS); // Generar un visitante cada 0.5 segundos.

        // Esperar a que el parque cierre.
        try {
            hiloParque.join(); // Esperar a que el hilo del parque termine (a las 23:00).
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("--- SIMULACIÓN FINALIZADA ---");
    }
}