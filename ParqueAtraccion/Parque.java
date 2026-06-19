package ParqueAtraccion;

import ParqueAtraccion.Atracciones.*;

public class Parque {
    // Volatile asegura que todos los hilos vean el cambio de estado al instante al hacer
    // lo que permite que la maquina virtual de java no guarde en la memoria cache sino de la memoria principal.
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
        // Pasamos el parque para que ellas puedan consultar la hora y el estado.
        this.trenTuristico = new TrenTuristico(this);
        this.autitosChocadores = new AutitosChocadores(this);
        this.barcoPirata = new BarcoPirata(this);
        this.montanaRusa = new MontanaRusa(this);
        this.realidadVirtual = new RealidadVirtual(this);
        this.juegoPremios = new JuegoPremios(this);
        this.comedor = new Comedor(this);
    }

    public boolean isParqueAbierto() { return parqueAbierto; }
    public boolean isIngresoAbierto() { return ingresoAbierto; }
    public boolean estanActividadesAbiertas() { return actividadesAbiertas; }
    public int getHoraSimulada() { return horaSimulada; }

    public void avanzarHora() { this.horaSimulada++; }
    public void cerrarIngreso() { this.ingresoAbierto = false; }
    public void cerrarActividades() { this.actividadesAbiertas = false; }
    public void cerrarParqueDefinitivamente() { this.parqueAbierto = false; }

    public TrenTuristico getTrenTuristico() { return trenTuristico; }
    public AutitosChocadores getAutitosChocadores() { return autitosChocadores; }
    public BarcoPirata getBarcoPirata() { return barcoPirata; }
    public MontanaRusa getMontanaRusa() { return montanaRusa; }
    public RealidadVirtual getRealidadVirtual() { return realidadVirtual; }
    public JuegoPremios getJuegoPremios() { return juegoPremios; }
    public Comedor getComedor() { return comedor; }
}