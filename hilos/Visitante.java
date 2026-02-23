package hilos;

import ParqueAtraccion.Parque;
import java.util.Random;

public class Visitante implements Runnable {

    private final String nombre;
    private final Parque parque;
    private final Random random = new Random();

    public Visitante(String nombre, Parque parque) {
        this.nombre = nombre;
        this.parque = parque;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void run() {
        System.out.println("[INGRESO]: " + nombre + " entró al parque.");

        try {
            // Sigue dando vueltas mientras las atracciones sigan abiertas.
            while (parque.estanActividadesAbiertas()) {
                
                int eleccion = random.nextInt(5);

                switch (eleccion) {
                    case 0:
                        parque.getTrenTuristico().subir(this);
                        break;
                    case 1:
                        parque.getAutitosChocadores().jugar(this);
                        break;
                    case 2:
                        parque.getBarcoPirata().subir(this);
                        break;
                    case 3:
                        parque.getMontanaRusa().subir(this);
                        break;
                    case 4:
                        parque.getRealidadVirtual().jugar(this);
                        break;
                }

                // Si salió del juego y el parque sigue abierto, camina un rato hasta el próximo.
                if (parque.estanActividadesAbiertas()) {
                    Thread.sleep(1000 + random.nextInt(2000));
                }
            }

            System.out.println("[SALIDA]: " + nombre + " se va del parque porque cerraron las atracciones.");

        } catch (InterruptedException e) {
            System.out.println("[SALIDA]: " + nombre + " fue interrumpido y se va.");
            Thread.currentThread().interrupt();
        }
    }
}


