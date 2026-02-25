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
                int eleccion = random.nextInt(7);

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
                    case 5:
                        parque.getJuegoPremios().jugar(this);
                        break;
                    case 6:
                        parque.getComedor().comer(this);
                        break;
                }
                
                // Si el parque sigue abierto, el visitante camina y descansa un poco antes de la siguiente atracción.
                if (parque.estanActividadesAbiertas()) {
                    Thread.sleep(750 + random.nextInt(500));
                }
            }
            
            System.out.println("[VISITANTE]: " + nombre + " se retira porque terminaron las actividades (19:00).");
            
        } catch (InterruptedException e) {
            // Si el visitante estaba esperando en una atracción y el parque cierra/interrumpe los hilos, cae directo acá.
            System.out.println("[VISITANTE]: " + nombre + " fue interrumpido/evacuado y se retira del parque.");
            Thread.currentThread().interrupt();
        }
    }
}

