package ParqueAtraccion.Atracciones;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import hilos.Visitante;

public class AutitosChocadores {

    private static final int autosTotales = 10;
    private static final int personasXAuto = 2;
    private static final int personasRequeridas = autosTotales * personasXAuto;

    private int contadorPersonas = 0;
    private boolean juegoEnCurso = false;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition puedeIniciar = lock.newCondition();

    public void subir(Visitante visitante) {
        String nombre = visitante.getNombre();
        boolean soyElUltimo = false;

        lock.lock();
        try {
            System.out.println("[AC]: " + nombre + " llega a Autitos Chocadores (" +
                            contadorPersonas + "/" + personasRequeridas + ")");

            while (juegoEnCurso) {
                puedeIniciar.await();
            }

            contadorPersonas++;

            if (contadorPersonas == personasRequeridas) {
                juegoEnCurso = true;
                soyElUltimo = true;
                System.out.println("[AC]: ¡" + personasRequeridas + " personas para jugar!"
                                    + " INICIA EL JUEGO. >:D ");
                puedeIniciar.signalAll();
            } else {
                while (!juegoEnCurso) {
                    puedeIniciar.await();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        // JUEGO FUERA DEL LOCK
        if (soyElUltimo) {
            try {
                Thread.sleep(3000);
                System.out.println("[AC]: Autitos Chocadores FINALIZAN JUEGO");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock.lock();
            try {
                contadorPersonas = 0;
                juegoEnCurso = false;
                puedeIniciar.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
