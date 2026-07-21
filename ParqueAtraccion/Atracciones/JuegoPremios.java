package ParqueAtraccion.Atracciones;

import hilos.Visitante;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JuegoPremios implements Runnable {
    private final Exchanger<String> intercambiador = new Exchanger<>();

    // Ciclo del visitante para cambiar ficha por premio.
    public void intercambiar(Visitante visitante) {
        String ficha = "Ficha de " + visitante.getNombre();
        System.out.println("[PREMIOS]: " + visitante.getNombre() + " se acerca con su ficha.");
        realizarIntercambio(ficha);
    }

    // Lógica del Exchanger.
    private void realizarIntercambio(String ficha) {
        try {
            String premio = intercambiador.exchange(ficha, 2, TimeUnit.SECONDS);
            System.out.println("[PREMIOS]: Recibido -> " + premio);
        } catch (InterruptedException | TimeoutException e) {
            System.out.println("[PREMIOS]: No había nadie para atender, el visitante se retiró.");
        }
    }

    // Ciclo del empleado del puesto.
    @Override
    public void run() {
        while (true) {
            atenderPuesto();
        }
    }

    // Espera a un visitante para dar el peluche.
    private void atenderPuesto() {
        try {
            String recibido = intercambiador.exchange("Oso de Peluche", 2, TimeUnit.SECONDS);
            System.out.println("[EMPLEADO_PREMIOS]: Recibí una " + recibido + " y entregué un premio.");
        } catch (InterruptedException | TimeoutException e) {
            // El empleado espera en silencio.
        }
    }
}