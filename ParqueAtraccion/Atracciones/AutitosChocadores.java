package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutitosChocadores {

    
    private final Lock cerrojo = new ReentrantLock();
    
    private final Condition esperaJuego = cerrojo.newCondition(); // Condición para esperar a que se llenen los 20 o para esperar a que se vacíe la pista.
    
    private int pasajerosActuales = 0;
    private final int capacidadMaxima = 20;

    public void jugar(Visitante visitante) {
        String nombre = visitante.getNombre();
        
        cerrojo.lock(); //  Bloqueamos.
        try {
            // Mientras la atraccion esté llena, hace esperar afuera.
            while (pasajerosActuales >= capacidadMaxima) {
                esperaJuego.await();
            }

            // Ingreso a la pista.
            pasajerosActuales++;
            System.out.println("[AUTITOS]: " + nombre + " eligió su auto (" + pasajerosActuales + "/" + capacidadMaxima + ")");

            if (pasajerosActuales < capacidadMaxima) {
                // Espero a que se llenen los 20.
                try {
                    while (pasajerosActuales < capacidadMaxima) {
                        esperaJuego.await(); // Libera el lock y se duerme.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                // el ultimo(el 20) Arranca el juego.
                System.out.println("[AUTITOS]: ¡Autos listos! COMIENZAN LOS CHOQUES.");
                
                try {
                    Thread.sleep(2500); // Simulamos el juego.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.println("[AUTITOS]: Fin del juego. Todos bajan.");
                
                // Despierto a todos los que están durmiendo en el await().
                esperaJuego.signalAll();
            }

            pasajerosActuales--;
            System.out.println("[AUTITOS]: " + nombre + " libera su auto.");

            // Si soy el último en irme, dejo la pista vacía para el siguiente grupo.
            if (pasajerosActuales == 0) {
                System.out.println("[AUTITOS]: Pista vacía. Listos para el siguiente grupo.");
                esperaJuego.signalAll(); // Aviso a los que esperaban entrar al principio. <-pensar con otra condicion.
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            cerrojo.unlock(); //libero el lock.
        }
    }
}