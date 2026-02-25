package ParqueAtraccion.Atracciones;
import ParqueAtraccion.Parque;
import hilos.Visitante;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrenTuristico implements Runnable {
    //BlockingQueue para simular la fila del tren, con capacidad para 10 visitantes y un timeout de 4 segundos para evitar esperas eternas
    private final BlockingQueue<Visitante> fila = new ArrayBlockingQueue<>(10);
    private final Parque parque;

    public TrenTuristico(Parque parque) { this.parque = parque; }

    public void subir(Visitante visitante) {
        try {
            boolean enFila = fila.offer(visitante, 4, TimeUnit.SECONDS);
            if (!enFila) {
                System.out.println("[TREN]: " + visitante.getNombre() + " no encontró lugar y se fue.");
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @Override
    public void run() {
        try {
            while (parque.isParqueAbierto()) {
                Thread.sleep(5000);
                if (!fila.isEmpty()) {// Si hay visitantes en la fila, el tren realiza su recorrido
                    System.out.println("[TREN]: El tren realiza su recorrido.");
                    fila.clear();// Simula que el tren se va con los visitantes y vuelve vacío para la siguiente ronda
                }
            }
        } catch (InterruptedException e) {  System.out.println("[TREN]: El tren fue interrumpido."); 
                                            Thread.currentThread().interrupt(); }
    }
}