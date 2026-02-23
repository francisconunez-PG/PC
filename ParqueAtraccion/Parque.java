package ParqueAtraccion;

import ParqueAtraccion.Atracciones.*;

public class Parque implements Runnable {

    // volatile para que todos los hilos vean el cambio de hora al instante
    private volatile boolean parqueAbierto = true;
    private volatile boolean ingresoAbierto = true;
    private volatile boolean actividadesAbiertas = true;
    
    private int horaSimulada = 9;

    // Declaramos las atracciones
    private final TrenTuristico trenTuristico;
    private final AutitosChocadores autitosChocadores;
    private final BarcoPirata barcoPirata;
    private final MontanaRusa montanaRusa;
    private final RealidadVirtual realidadVirtual;
    private final JuegoPremios juegoPremios;
    private final Comedor comedor;

    public Parque() {
        // Le pasamos "this" a todas para que sepan en qué parque están
        this.trenTuristico = new TrenTuristico(this);
        this.autitosChocadores = new AutitosChocadores(this);
        this.barcoPirata = new BarcoPirata(this);
        this.montanaRusa = new MontanaRusa(this);
        this.realidadVirtual = new RealidadVirtual(this);
        this.juegoPremios = new JuegoPremios(this);
        this.comedor = new Comedor(this);
    }

    // Getters
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
            while (parqueAbierto) {
                Thread.sleep(5000); // 5 segundos reales = 1 hora simulada.
                horaSimulada++;

                if (horaSimulada == 18) {
                    ingresoAbierto = false;
                    System.out.println("¡ATENCIÓN! El ingreso al parque ha CERRADO (18:00 hrs).");
                }
                if (horaSimulada == 19) {
                    actividadesAbiertas = false;
                    System.out.println("¡ATENCIÓN! Todas las actividades han CERRADO (19:00 hrs).");
                }
                if (horaSimulada >= 23) {
                    parqueAbierto = false;
                    System.out.println("¡FIN DE JORNADA! El parque CIERRA completamente (23:00 hrs).");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}