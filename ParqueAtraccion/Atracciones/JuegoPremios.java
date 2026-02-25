package ParqueAtraccion.Atracciones;
import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class JuegoPremios {
    //Exchanger para simular el intercambio de fichas por premios en el mostrador.
    //El visitante entrega su ficha y recibe un premio a cambio.
    //Si no hay personal atendiendo o si el parque cierra, el visitante se va sin premio.
    private final Exchanger<String> mostrador = new Exchanger<>();
    private final Parque parque;

    public JuegoPremios(Parque parque) { this.parque = parque; }

    public void jugar(Visitante visitante) {
        try {
            if (parque.estanActividadesAbiertas()) {
                mostrador.exchange("Ficha", 3, TimeUnit.SECONDS); // El visitante entrega su ficha y espera a recibir su premio. Si no recibe el premio en 3 segundos, se va sin él.
                System.out.println("[PREMIOS]: " + visitante.getNombre() + " canjeó su premio.");
            }
        } catch (Exception e) {
            System.out.println("[PREMIOS]: " + visitante.getNombre() + " se fue sin premio.");
        }
    }

    public void atender() {
        try {
            while (parque.estanActividadesAbiertas()) {
                try {   // El personal del mostrador espera a que un visitante entregue su ficha.
                    mostrador.exchange("Oso de peluche", 2, TimeUnit.SECONDS); // Si no llega ningún visitante en 2 segundos, se queda esperando.
                } catch (Exception e) {System.out.println("[PREMIOS]: No hay visitantes en el mostrador."); }
            }
        } catch (Exception e) { System.out.println("[PREMIOS]: El mostrador cierra.");
                                Thread.currentThread().interrupt(); }
    }
}