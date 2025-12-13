package Atracciones;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AutitosChocadores {

    // Variables de Estado.
    private static final int autosTotales = 10;
    private static final int personasXAuto = 2;
    private static final int personasRequeridas = autosTotales * personasXAuto; // 20 personas en total.

    private int contadorPersonas; // Contador de personas esperando.
    private final ReentrantLock candado;
    private final Condition condicionInicioJuego;

    public AutitosChocadores() {
        this.contadorPersonas = 0;
        this.candado = new ReentrantLock();
        this.condicionInicioJuego = candado.newCondition();
    }
    
    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();
        candado.lock(); // Adquirir el candado.
        
        try {
            System.out.println(nombre + "llega a Autitos Chocadores. Esperando: " + contadorPersonas + "/" + personasRequeridas);
            contadorPersonas++;

            if (contadorPersonas < personasRequeridas) {
                // Si aún no hay 20 personas, esperar la condición.
                System.out.println(nombre + " espera a que lleguen los demás para completar los 20.");
                condicionInicioJuego.await(); // Espera y libera el candado.
                // Cuando despierta, continúa el viaje.
                System.out.println(nombre + " despierta. ¡Juego INICIA!");

            } else {
                // Si el contador llega a 20.
                System.out.println("¡20 PERSONAS COMPLETAS! Autitos Chocadores... ¡INICIAN JUEGO!");
                
                // Despertar a todos los hilos que están esperando.
                condicionInicioJuego.signalAll();
                
                // Simular el juego.
                Thread.sleep(3000); // 3 segundos de juego.
                
                // Reiniciar el estado
                contadorPersonas = 0;
                System.out.println("Autitos Chocadores FINALIZAN JUEGO. Reiniciando contador.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            candado.unlock(); // Se libera el candado.
        }
    }
}