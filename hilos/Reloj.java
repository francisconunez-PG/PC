package hilos;

import ParqueAtraccion.Parque;

public class Reloj implements Runnable {
    private final Parque parque;

    public Reloj(Parque parque) {
        this.parque = parque;
    }

    @Override
    public void run() {
        try {
            System.out.println("=== EL PARQUE ABRE SUS PUERTAS (09:00) ===");
            
            while (parque.isParqueAbierto()) {
                // Cada 5 segundos avanza una hora en la simulación.
                Thread.sleep(5000);
                parque.avanzarHora();
                
                int horaActual = parque.getHoraSimulada();
                // Imprimo la hora para darle contexto a los logs de consola.
                System.out.println("\n[RELOJ]: Son las " + horaActual + ":00 hs.");
                
                if (horaActual == 18) {
                    parque.cerrarIngreso();
                    System.out.println("--- El ingreso al parque se ha cerrado (18:00). No entran más visitantes. ---");
                }
                
                if (horaActual == 19) {
                    parque.cerrarActividades();
                    System.out.println("--- Las actividades han finalizado (19:00). Los visitantes deben ir a la salida. ---");
                }
                
                if (horaActual == 23) {
                    parque.cerrarParqueDefinitivamente();
                    System.out.println("--- El parque cierra sus puertas definitivamente (23:00) ---");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[RELOJ]: El reloj del parque fue interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}
