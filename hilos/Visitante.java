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
        try {
            while (parque.estanActividadesAbiertas()) {
                elegirYParticipar();
                descansarEntreJuegos();
            }
            System.out.println("[VISITANTE]: " + nombre + " se retira por cierre de actividades.");
        } catch (InterruptedException e) {
            System.out.println("[VISITANTE]: " + nombre + " fue evacuado del parque.");
            Thread.currentThread().interrupt();
        }
    }

    // Selecciona una atracción al azar.
    private void elegirYParticipar() {
        int eleccion = random.nextInt(7);
        
        switch (eleccion) {
            case 0 -> parque.getMontanaRusa().subir(this);
            case 1 -> parque.getBarcoPirata().subir(this);
            case 2 -> parque.getAutitosChocadores().subir(this);
            case 3 -> parque.getTrenTuristico().viajar(this);
            case 4 -> parque.getRealidadVirtual().jugar(this);
            case 5 -> parque.getComedor().comer(this);
            case 6 -> parque.getJuegoPremios().intercambiar(this);
        }
    }

    // Simula el tiempo de caminata entre juegos.
    private void descansarEntreJuegos() throws InterruptedException {
        if (parque.estanActividadesAbiertas()) {
            Thread.sleep(150 + random.nextInt(400));
        }
    }
}

