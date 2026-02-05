package hilos;

import ParqueAtraccion.Parque;
import java.util.Random;

public class Visitante implements Runnable {

    //Variables de Estado.
    private final String nombre;
    private final Parque parque; // Gestor de horarios y acceso a atracciones.
    private final Random aleatorio = new Random();

    // Constructor que recibe el nombre y la referencia al Parque.
    public Visitante(String nombre, Parque parque) {
        this.nombre = nombre;
        this.parque = parque;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void run() {
        System.out.println(nombre + " ha ingresado al parque a través de un molinete.");

        // El visitante se mueve por el parque mientras las actividades estén abiertas.
        while (parque.estanActividadesAbiertas()) {
            try {
                // 1. Decidir aleatoriamente qué atracción visitar.
                int eleccion = aleatorio.nextInt(7); // 0 a 6.

                switch (eleccion) {
                    case 0:
                        System.out.println(nombre + " se dirige a la Montaña Rusa.");
                        parque.getMontanaRusa().subir(this);
                        break;
                    case 1:
                        System.out.println(nombre + " se dirige a los Autitos Chocadores.");
                        parque.getAutitosChocadores().subir(this);
                        break;
                    case 2:
                        System.out.println(nombre + " se dirige al Comedor.");
                        parque.getComedor().almorzar(this);
                        break;
                    case 3:
                        System.out.println(nombre + " se dirige a la cola del Tren Turístico.");
                        parque.getTrenTuristico().subirAlTren(this);
                        break;
                    case 4:
                        System.out.println(nombre + " se dirige al Área de Juegos de Premios (Exchanger).");
                        // Aquí simulamos el intercambio de una ficha por un premio.
                        String premioObtenido = parque.getJuegoPremios().participar(this, "Ficha de " + nombre);
                        System.out.println(nombre + " obtuvo un " + premioObtenido + " en el juego.");
                        break;
                    case 5:
                        System.out.println(nombre + " se dirige a la Realidad Virtual.");
                        parque.getRealidadVirtual().participar(this);
                        break;
                    case 6:
                        System.out.println(nombre + " se dirige al Barco Pirata.");
                        parque.getBarcoPirata().subir(this);
                        break;

                }
                
                Thread.sleep(aleatorio.nextInt(1000) + 500); // Descansa entre 0.5 y 1.5 segundos.

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        } // Fin del ciclo de actividades.
        //Thread th = Thread.currentThread();
        //System.out.println("Estado del hilo: " + th.getState());
        // Salida del parque.
        System.out.println(nombre + " ha terminado sus actividades (19:00 hrs) y se dirige a la salida. ¡Hasta luego!");
        
    }
}