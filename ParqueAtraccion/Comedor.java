package ParqueAtraccion;

import hilos.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Comedor {
    // CyclicBarrier para esperar a 4 visitantes por mesa.
    private final CyclicBarrier mesa = new CyclicBarrier(4, () -> {
        System.out.println("[COMEDOR]: Mesa de 4 completa. ¡Empiezan a comer!");
    });
    private final Parque parque;

    public Comedor(Parque parque) {
        this.parque = parque;
    }

    public void comer(Visitante visitante) {
        boolean pudoSentarse = false;
        
        if (parque.estanActividadesAbiertas()) {
            System.out.println("[COMEDOR]: " + visitante.getNombre() + " se sentó y espera compañía para comer.");
            
            try {
                // Espera hasta 5 segundos a que se llene la mesa
                mesa.await(5, TimeUnit.SECONDS);
                pudoSentarse = true;
                
            } catch (TimeoutException e) {
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " se cansó de esperar y se fue (Timeout).");
                // Uno se va, se cagan todos.
                mesa.reset();
                
            } catch (BrokenBarrierException e) {
                // Los que estaban esperando cuando el uno se fue por impaciente, caen acá.
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " se fue porque la mesa se desarmó.");
                
            } catch (InterruptedException e) {
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " fue interrumpido antes de comer.");
                Thread.currentThread().interrupt();
            }
        }

        // Solo los que lograron formar el grupo de 4 entran a comer
        if (pudoSentarse) {
            try {
                // Simula el tiempo que tardan en comer todos juntos
                Thread.sleep(2500);
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " terminó su comida.");
            } catch (InterruptedException e) {
                System.out.println("[COMEDOR]: " + visitante.getNombre() + " tuvo que evacuar el comedor.");
                Thread.currentThread().interrupt();
            }
        }
    }
}