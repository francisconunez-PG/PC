package ParqueAtraccion;

import ParqueAtraccion.Atracciones.*;

public class Parque implements Runnable {
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

    public Parque() {// El parque se encarga de crear las atracciones y pasarles su referencia para que puedan consultar el estado del parque.
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
            while (parqueAbierto) {
                Thread.sleep(5000); // Cada 5 segundos avanza una hora en la simulación.
                horaSimulada++;
                
                if (horaSimulada == 18) {
                    ingresoAbierto = false;
                    System.out.println("--- El ingreso al parque se ha cerrado (18:00) ---");
                }
                
                if (horaSimulada == 19) {
                    actividadesAbiertas = false;
                    System.out.println("--- Las actividades han finalizado (19:00) ---");
                }
                
                if (horaSimulada == 23) {
                    parqueAbierto = false;
                    System.out.println("--- El parque cierra sus puertas definitivamente (23:00) ---");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}