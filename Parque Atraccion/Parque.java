import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Parque implements Runnable {

    // Instancias de Atracciones.
    private final MontanaRusa montanaRusa = new MontanaRusa();
    private final AutitosChocadores autitosChocadores = new AutitosChocadores();
    private final Comedor comedor = new Comedor();
    private final TrenTuristico trenTuristico = new TrenTuristico();
    private final RealidadVirtual realidadVirtual = new RealidadVirtual(5, 10, 5); // 5 Equipos completos.
    private final JuegoPremios juegoPremios = new JuegoPremios(); // crear esta clase con Exchanger.

    // Variables de control de horario.
    private volatile boolean ingresoAbierto = true; // Cierra 18:00
    private volatile boolean actividadesAbiertas = true; // Cierra 19:00
    private volatile boolean parqueAbierto = true; // Cierra 23:00
    private int horaSimulada = 9;

    // Métodos Getters.
    public MontanaRusa getMontanaRusa() { return montanaRusa; }
    public AutitosChocadores getAutitosChocadores() { return autitosChocadores; }
    public Comedor getComedor() { return comedor; }
    public TrenTuristico getTrenTuristico() { return trenTuristico; }
    public RealidadVirtual getRealidadVirtual() { return realidadVirtual; }
    public JuegoPremios getJuegoPremios() { return juegoPremios; }
    public boolean estanActividadesAbiertas() { return actividadesAbiertas; }
    // ... más getters

    // Control de Horarios.
    @Override
    public void run() {
        // ... Lógica de horaSimulada, cierre a las 18, 19 y 23.
        try {
            while (parqueAbierto) {
                Thread.sleep(1000); // 1 segundo real = 1 hora simulada.
                horaSimulada++;

                if (horaSimulada == 18) { ingresoAbierto = false; System.out.println("--- ¡ATENCIÓN! El ingreso al parque ha CERRADO (18:00 hrs). ---"); }
                if (horaSimulada == 19) { actividadesAbiertas = false; System.out.println("--- ¡ATENCIÓN! Todas las actividades han CERRADO (19:00 hrs). ---"); }
                if (horaSimulada >= 23) { parqueAbierto = false; System.out.println("--- ¡FIN DE JORNADA! El parque CIERRA completamente (23:00 hrs). ---"); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}