package ParqueAtraccion.Atracciones;

import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JuegoPremios {
    private final Exchanger<String> mostrador = new Exchanger<>();
    private final Parque parque;

    public JuegoPremios(Parque parque) {
        this.parque = parque;
    }

    public void jugar(Visitante visitante) {
        if (parque.estanActividadesAbiertas()) {
            try {
                // Intercambio seguro en un solo paso
                String premio = mostrador.exchange("Ficha", 3, TimeUnit.SECONDS);
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " entregó su ficha y se ganó un " + premio + ".");
            } catch (TimeoutException e) {
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " se cansó de esperar y se fue sin premio.");
            } catch (InterruptedException e) {
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " se tuvo que ir por cierre del parque.");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void atender() {
        while (parque.estanActividadesAbiertas()) {
            try {
                mostrador.exchange("Oso de peluche", 2, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println("[PREMIOS]: El empleado se aburrió de esperar y sigue dando vueltas.");
            } catch (InterruptedException e) {
                System.out.println("[PREMIOS]: El empleado cierra el mostrador y se va.");
                Thread.currentThread().interrupt();
            }
        }
    }
}