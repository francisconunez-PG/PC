package main;

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

        JuegoPremios premios = new JuegoPremios();
        Thread encargadoPremios = new Thread(premios);
        encargadoPremios.setDaemon(true);
        encargadoPremios.start();
    }

    // Lanza los controladores de las máquinas.
    private static void iniciarAtracciones(Parque parque) {
        new Thread(new TrenTuristico(parque)).start();
        new Thread(new AutitosChocadores(parque)).start();
        new Thread(new BarcoPirata(parque)).start();
        new Thread(new MontanaRusa(parque)).start();
        new Thread(new RealidadVirtual(parque)).start();
        new Thread(new Comedor(parque)).start();
    }

    // Simula la llegada escalonada de personas.
    private static void recibirVisitantes(Parque parque) {
        for (int i = 1; i <= 30; i++) {
            if (!parque.isIngresoAbierto()) break;

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