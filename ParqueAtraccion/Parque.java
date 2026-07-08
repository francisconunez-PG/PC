package ParqueAtraccion;

import ParqueAtraccion.Atracciones.*;

public class Parque {
    private volatile boolean parqueAbierto = true;
    private volatile boolean ingresoAbierto = true;
    private volatile boolean actividadesAbiertas = true;

    private final MontanaRusa montanaRusa = new MontanaRusa(this);
    private final BarcoPirata barcoPirata = new BarcoPirata(this);
    private final AutitosChocadores autitosChocadores = new AutitosChocadores(this);
    private final TrenTuristico trenTuristico = new TrenTuristico(this);
    private final RealidadVirtual realidadVirtual = new RealidadVirtual(this);
    private final Comedor comedor = new Comedor(this);
    private final JuegoPremios juegoPremios = new JuegoPremios();

    // Métodos de estado del parque.
    public boolean isParqueAbierto() { return parqueAbierto; }
    public boolean isIngresoAbierto() { return ingresoAbierto; }
    public boolean estanActividadesAbiertas() { return actividadesAbiertas; }

    // Cambios de estado.
    public void cerrarIngreso() { this.ingresoAbierto = false; }
    public void cerrarActividades() { this.actividadesAbiertas = false; }
    public void cerrarParque() { this.parqueAbierto = false; }

    // Getters de atracciones.
    public MontanaRusa getMontanaRusa() { return montanaRusa; }
    public BarcoPirata getBarcoPirata() { return barcoPirata; }
    public AutitosChocadores getAutitosChocadores() { return autitosChocadores; }
    public TrenTuristico getTrenTuristico() { return trenTuristico; }
    public RealidadVirtual getRealidadVirtual() { return realidadVirtual; }
    public Comedor getComedor() { return comedor; }
    public JuegoPremios getJuegoPremios() { return juegoPremios; }
}