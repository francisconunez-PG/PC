package ParqueAtraccion;

import ParqueAtraccion.Atracciones.*;

public class Parque implements Runnable {
    // Volatile asegura que todos los hilos vean el cambio de estado al instante al hacer
    // que la maquina virtual de java no guarde en la memoria cache sino de la memoria principal.
    private volatile boolean parqueAbierto = true;
    private volatile boolean ingresoAbierto = true;
    private volatile boolean actividadesAbiertas = true;
    private int horaSimulada = 9;

    private final TrenTuristico trenTuristico;
    private final AutitosChocadores autitosChocadores;
    private final BarcoPirata barcoPirata;
    private final MontanaRusa montanaRusa;
    private final RealidadVirtual realidadVirtual;
    private final JuegoPremios juegoPremios;
    private final Comedor comedor;

    public Parque() {

        // para que ellas puedan consultar la hora y el estado.
        this.trenTuristico = new TrenTuristico(this);
        this.autitosChocadores = new AutitosChocadores(this);
        this.barcoPirata = new BarcoPirata();
        this.montanaRusa = new MontanaRusa(this);
        this.realidadVirtual = new RealidadVirtual(this);
        this.juegoPremios = new JuegoPremios(this);
        this.comedor = new Comedor(this);
    }

    public boolean isParqueAbierto() { return parqueAbierto; }
    public boolean isIngresoAbierto() { return ingresoAbierto; }
    public boolean estanActividadesAbiertas() { return actividadesAbiertas; }

    public TrenTuristico getTrenTuristico() { return trenTuristico; }
    public AutitosChocadores getAutitosChocadores() { return autitosChocadores; }
    public BarcoPirata getBarcoPirata() { return barcoPirata; }
    public MontanaRusa getMontanaRusa() { return montanaRusa; }
    public RealidadVirtual getRealidadVirtual() { return realidadVirtual; }
    public JuegoPremios getJuegoPremios() { return juegoPremios; }
    public Comedor getComedor() { return comedor; }

    @Override
    public void run() {
        try {
            System.out.println("=== EL PARQUE ABRE SUS PUERTAS (09:00) ===");
            
            while (parqueAbierto) {
                // Cada 5 segundos avanza una hora en la simulación.
                Thread.sleep(5000);
                horaSimulada++;
                
                // Imprimimos la hora para darle contexto a los logs de consola.
                System.out.println("\n[RELOJ]: Son las " + horaSimulada + ":00 hs.");
                
                if (horaSimulada == 18) {
                    ingresoAbierto = false;
                    System.out.println("--- El ingreso al parque se ha cerrado (18:00). No entran más visitantes. ---");
                }
                
                if (horaSimulada == 19) {
                    actividadesAbiertas = false;
                    System.out.println("--- Las actividades han finalizado (19:00). Los visitantes deben ir a la salida. ---");
                }
                
                if (horaSimulada == 23) {
                    parqueAbierto = false;
                    System.out.println("--- El parque cierra sus puertas definitivamente (23:00) ---");
                }
            }
        }catch (InterruptedException e) {
            System.out.println("[RELOJ]: El reloj del parque fue interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}