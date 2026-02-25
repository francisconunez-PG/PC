package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JuegoPremios {
    // Usamos el exchanger para que el visitante y el empleado se pasen las cosas al mismo tiempo.
    private final Exchanger<String> mostrador = new Exchanger<>();
    private final Parque parque;

    public JuegoPremios(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        if (parque.estanActividadesAbiertas()) {
            try {
                // Le damos la ficha al empleado y esperamos hasta 3 segundos.
                // Lo que nos devuelve lo guardamos para mostrarlo en consola.
                String premio = mostrador.exchange("Ficha", 3, TimeUnit.SECONDS);
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " entregó su ficha y se ganó un " + premio + ".");
            } catch (TimeoutException e) {
                // Si pasaron los 3 segundos y el empleado estaba ocupado, nos vamos sin nada.
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " se cansó de esperar y se fue sin premio.");
            } catch (InterruptedException e) {
                // Si justo cierran el parque mientras esperábamos, cortamos la espera.
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " se tuvo que ir por cierre del parque.");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void atender() {
        // El empleado atiende dando vueltas acá mientras el parque siga abierto.
        while (parque.estanActividadesAbiertas()) {
            try {
                // El empleado se queda 2 segundos en el mostrador con el peluche en la mano ofreciéndolo.
                mostrador.exchange("Oso de peluche", 2, TimeUnit.SECONDS);
                
            } catch (TimeoutException e) {
                // Si nadie vino a canjear una ficha en esos 2 segundos, el empleado se aburre y sigue dando vueltas.
                System.out.println("[PREMIOS]: El empleado se aburrió de esperar y sigue dando vueltas.");
            } catch (InterruptedException e) {
                // Le "avisan" al empleado que terminó su turno y marcamos el hilo como interrumpido.
                System.out.println("[PREMIOS]: El empleado cierra el mostrador y se va.");
                Thread.currentThread().interrupt();
            }
        }
    }
}