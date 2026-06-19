package Main;

import ParqueAtraccion.Parque;
import hilos.Reloj;
import hilos.Visitante;

public class Main {
    public static void main(String[] args) {
        Parque parque = new Parque();
        
        // Hilo reloj que controla el tiempo del parque.
        Thread Reloj = new Thread(new Reloj(parque));
        Reloj.start();
        
        // Arrancamos los hilos de las atracciones para que manejen sus propios ciclos y simulación de tiempo.
        Thread hiloTren = new Thread(parque.getTrenTuristico());
        hiloTren.start();
        
        Thread hiloMontana = new Thread(parque.getMontanaRusa());
        hiloMontana.start();
        
        Thread hiloBarco = new Thread(parque.getBarcoPirata());
        hiloBarco.start();
        
        Thread hiloAutitos = new Thread(parque.getAutitosChocadores());
        hiloAutitos.start();
        
        Thread hiloRV = new Thread(parque.getRealidadVirtual());
        hiloRV.start();
        
        // Hilo encargado de los premios (usa Exchanger).
        Thread encargadoPremios = new Thread(() -> {
            parque.getJuegoPremios().atender();
        });
        encargadoPremios.setDaemon(true); // Se marca como daemon para que no impida el cierre del programa.
        encargadoPremios.start();

        // Se crean visitantes mientras el ingreso esté permitido y no se supere el cupo.
        for (int i = 1; i <= 30 && parque.isIngresoAbierto(); i++) {
            Thread visitanThread = new Thread(new Visitante("Visitante-" + i, parque));
            visitanThread.start();
            
            try {
                // Llega un visitante nuevo cada 1.5 segundos
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("--- El sistema ha dejado de generar nuevos visitantes ---");
    }
}