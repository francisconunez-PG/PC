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

    public String getNombre() { return nombre; }

    @Override
    public void run() {
        try {
            // Mientras las actividades estén abiertas, el visitante elige juegos.
            while (parque.estanActividadesAbiertas()) {
                int eleccion = random.nextInt(6); // 6 opciones incluyendo el comedor.

                if (eleccion == 0) {
                    parque.getTrenTuristico().subir(this);
                } else if (eleccion == 1) {
                    parque.getAutitosChocadores().jugar(this);
                } else if (eleccion == 2) {
                    parque.getBarcoPirata().subir(this);
                } else if (eleccion == 3) {
                    parque.getMontanaRusa().subir(this);
                } else if (eleccion == 4) {
                    parque.getRealidadVirtual().jugar(this);
                } else {
                    parque.getComedor().comer(this);
                }
                
                // Si todavía hay tiempo, descansa un poco antes de la siguiente atracción.
                if (parque.estanActividadesAbiertas()) {
                    Thread.sleep(750 + random.nextInt(500));
                }
            }
            
            System.out.println("[VISITANTE]: " + nombre + " se retira del parque porque terminaron las actividades.");
            
        } catch (InterruptedException e) {
            System.out.println("[VISITANTE]: " + nombre + " fue interrumpido y se retira del parque.");
            Thread.currentThread().interrupt();
        }
    }
}

