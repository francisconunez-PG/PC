package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class AutitosChocadores {

    // Usamos una barrera para que los 20 pasajeros salgan todos juntos al chocar.
    private final CyclicBarrier inicioTurno = new CyclicBarrier(20, () -> {
        System.out.println("[AUTITOS]: Los 10 autos están completos. ¡Arranca el juego!");
    });
    
    // Semáforo para controlar que solo entren 20 personas a la pista.
    private final Semaphore capacidadPista = new Semaphore(20, true);

    public AutitosChocadores(Parque parque) {
        //TODO Auto-generated constructor stub
    }

    public void jugar(Visitante visitante) {
        boolean pudoSubirse = false;
        try {
            // El visitante intenta conseguir uno de los 20 lugares.
            pudoSubirse = capacidadPista.tryAcquire();
            
            if (pudoSubirse) {
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " se subió a un auto y espera al resto.");
                
                // Espera a que los otros 19 lugares se ocupen para que la barrera abra.
                inicioTurno.await();
                
                // Simulación del tiempo que están chocando.
                Thread.sleep(3000);
                
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " bajó del auto y libera el lugar.");
            } else {
                System.out.println("[AUTITOS]: " + visitante.getNombre() + " no encontró autos libres y se fue a otro lado.");
            }
            
        } catch (Exception e) { System.out.println("[AUTITOS]: " + visitante.getNombre() + " tuvo un problema y no pudo jugar.");
                                Thread.currentThread().interrupt();
        } finally {
            // El permiso se libera solo si efectivamente lo había adquirido.
            if (pudoSubirse) {
                capacidadPista.release();
            }
        }
    }
}