package ParqueAtraccion.Atracciones;
import ParqueAtraccion.Parque;
import hilos.Visitante;

public class MontanaRusa {
    private int pasajeros = 0;
    private int enEspera = 0;
    private boolean enViaje = false;
    private final int CAPACIDAD = 5; // Capacidad de la montaña rusa
    private final int limiteEspera = 10; // Límite de visitantes en espera para evitar filas eternas

    public MontanaRusa(Parque parque) {
        //TODO Auto-generated constructor stub
    }

    public synchronized void subir(Visitante visitante) throws InterruptedException {
        //Monitores para controlar el acceso a la montaña rusa. Si la fila de espera supera el límite, los visitantes siguientes se retiran.
        if (enEspera < limiteEspera) {
            enEspera++;
            while (pasajeros >= CAPACIDAD || enViaje) {
                wait();
            }
            enEspera--;
            pasajeros++;
            System.out.println("[MONTAÑA]: " + visitante.getNombre() + " subió (" + pasajeros + "/5).");

            if (pasajeros == CAPACIDAD) {
                enViaje = true;
                notifyAll();
            } else {
                while (!enViaje) {
                    wait(); // Espera a que se llene la montaña rusa o a que comience el viaje
                }
            }
            Thread.sleep(2000);
            pasajeros--;
            if (pasajeros == 0) {// Si se vacía la montaña rusa, se marca como disponible y se notifica a los visitantes en espera
                enViaje = false;
                notifyAll();
            }
        } else {
            System.out.println("[MONTAÑA]: " + visitante.getNombre() + " se retiró por fila llena.");
        }
    }
}