package Main;

import ParqueAtraccion.Parque;
import ParqueAtraccion.Atracciones.*;
import hilos.Reloj;
import hilos.Visitante;

public class Main {
    public static void main(String[] args) {
        Parque parque = new Parque();
        
        iniciarAdministracion(parque);
        iniciarAtracciones(parque);
        recibirVisitantes(parque);
    }

    // Inicia los hilos de control general.
    private static void iniciarAdministracion(Parque parque) {
        Thread reloj = new Thread(new Reloj(parque));
        reloj.start();

        JuegoPremios premios = parque.getJuegoPremios();
        Thread encargadoPremios = new Thread(premios);
        encargadoPremios.setDaemon(true);
        encargadoPremios.start();
    }

    // Lanza los controladores de las máquinas.
    private static void iniciarAtracciones(Parque parque) {
        new Thread(parque.getTrenTuristico()).start();
        new Thread(parque.getAutitosChocadores()).start();
        new Thread(parque.getBarcoPirata()).start();
        new Thread(parque.getMontanaRusa()).start();
        new Thread(parque.getRealidadVirtual()).start();
        new Thread(parque.getComedor()).start();
    }

    // Simula la llegada escalonada de personas sin usar break.
    private static void recibirVisitantes(Parque parque) {
        for (int i = 1; i <= 30 && parque.isIngresoAbierto(); i++) {
            Visitante visitante = new Visitante("Visitante-" + i, parque);
            new Thread(visitante).start();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}