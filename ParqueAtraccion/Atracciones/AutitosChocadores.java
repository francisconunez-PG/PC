package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class AutitosChocadores {

    // La barrera junta a las 20 personas y recién cuando llega el último dispara el mensaje.
    private final CyclicBarrier inicioTurno = new CyclicBarrier(20, () -> {
        System.out.println("[AUTITOS]: Los 20 autos están completos y arranca el juego.");
    });
    
    // El semáforo controla que solo entren 20 personas a la pista.
    private final Semaphore capacidadPista = new Semaphore(20, true);
    private final Parque parque;

    public AutitosChocadores(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        boolean pudoSubirse = false;
        
        try {
            // El visitante intenta conseguir uno de los 20 lugares sin quedarse a esperar si está lleno.
            pudoSubirse = capacidadPista.tryAcquire();
            
            if (pudoSubirse) {
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " se subió a un auto y espera al resto.");
                
                // Se queda esperando a que los otros 19 lugares se ocupen para arrancar.
                inicioTurno.await();
                
                // Esto simula el tiempo que están manejando y chocando.
                Thread.sleep(3000);
                
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " bajó del auto y libera el lugar.");
            } else {
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " no encontró autos libres y se fue a otro lado.");
            }
            
        } catch (InterruptedException e) {
            // Si cierran el parque nos interrumpen la espera y nos vamos.
            System.out.println("[AUTITOS]: " + visitante.getNombre() + " se tuvo que ir por cierre del parque.");
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            // Si la barrera se rompe porque alguien se fue, el viaje se cancela para los demás :( ).
            System.out.println("[AUTITOS]: " + visitante.getNombre() + " se bajó porque se canceló la vuelta.");
        } finally {

            if (pudoSubirse) {
                capacidadPista.release();
            }
        }
    }
}