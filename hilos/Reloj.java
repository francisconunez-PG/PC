package hilos;

import ParqueAtraccion.Parque;

public class Reloj implements Runnable {
    private final Parque parque;
    private int horaActual = 9;

    public Reloj(Parque parque) {
        this.parque = parque;
    }

    @Override
    public void run() {
        while (parque.isParqueAbierto()) {
            avanzarTiempo();
            verificarCierres();
        }
    }

    // Simula el transcurso de una hora.
    private void avanzarTiempo() {
        try {
            Thread.sleep(8000);
            horaActual++;
            System.out.println("[RELOJ]: Son las " + horaActual + ":00 hs.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Evalúa los estados del parque según la hora.
    private void verificarCierres() {
        switch (horaActual) {
            case 18:
                System.out.println("[RELOJ]: 18:00 hs - Se cierra el ingreso al parque.");
                parque.cerrarIngreso();
                break;
            case 19:
                System.out.println("[RELOJ]: 19:00 hs - Cierre de actividades. Evacuando...");
                parque.cerrarActividades();
                break;
            case 23:
                System.out.println("[RELOJ]: 23:00 hs - Cierre total del parque.");
                parque.cerrarParque();
                break;
            default:
                break;
        }
    }
}
