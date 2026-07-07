package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AutitosChocadores implements Runnable {
    private final Semaphore capacidadPista = new Semaphore(20, true); // Controla la capacidad de la pista (20 autos) con orden de llegada.


    private final Semaphore avisoArranque = new Semaphore(0); // Permite que la pista espere a que se llenen los 20 autos antes de arrancar.
    private final Semaphore viajeTerminado = new Semaphore(0); // Permite que los visitantes esperen a que termine el viaje para bajarse. Se liberan todos los permisos al finalizar.
    

    // Barrera para esperar a que se suban los 20 visitantes antes de arrancar el juego.
    // El barrier se ejecuta cuando se completa la barrera, avisando a la pista que arranque.
    private final CyclicBarrier inicioTurno = new CyclicBarrier(20, () -> {
        System.out.println("[AUTITOS]: Los 20 autos están completos. ¡Le avisamos a la pista para arrancar!");
        avisoArranque.release(); // Libera el permiso para despertar al hilo de la pista.
    });
    
    private final Parque parque;

    public AutitosChocadores(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        boolean pudoSubirse = false;
        try {
            pudoSubirse = capacidadPista.tryAcquire(); // Intenta subirse a la pista, si no hay lugar se va a otro juego.
            
            if (pudoSubirse) {
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " se subió a un auto y espera al resto.");
                
                // Espera a que lleguen los 20.
                inicioTurno.await();
                
                // Espera a que la pista (el hilo mismo) termine el tiempo y libere el semáforo
                viajeTerminado.acquire();
                
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " bajó del auto y libera el lugar.");
            } else {
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " no encontró autos libres y se fue a otro juego.");
            }
        } catch (InterruptedException e) {
            System.out.println("[AUTITOS]: " + visitante.getNombre() + " se tuvo que ir por cierre del parque.");
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            System.out.println("[AUTITOS]: " + visitante.getNombre() + " se bajó porque se canceló la vuelta.");
        } finally {
            if (pudoSubirse) {
                // Libera el permiso para que otro visitante pueda subirse a la pista.
                capacidadPista.release();
            }
        }
    }

    @Override
    public void run() {
        while (parque.estanActividadesAbiertas()) {
            try {
                // La pista espera que la barrera libere este permiso (cuando se juntan 20)
                if (avisoArranque.tryAcquire(2, TimeUnit.SECONDS)) {
                    System.out.println("[AUTITOS]: ¡Arranca el juego! Chocando durante 3 segundos...");
                    Thread.sleep(3000); // El hilo de la máquina simula el viaje
                    System.out.println("[AUTITOS]: ¡Fin del juego! Se corta la corriente de la pista.");
                    
                    // Libera 20 permisos exactos para despertar a los 20 visitantes que esperan
                    viajeTerminado.release(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        viajeTerminado.release(20); // Liberar atrapados si el parque cierra
    }
}