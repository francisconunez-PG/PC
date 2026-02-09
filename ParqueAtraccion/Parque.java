package ParqueAtraccion;

import ParqueAtraccion.Atracciones.*;

public class Parque implements Runnable {

    // Instancias de Atracciones.
    private final MontanaRusa montanaRusa;  // Usa monitor y semáforo para sala de espera.
    private final AutitosChocadores autitosChocadores; //Usa cerrojo.
    private final Comedor comedor = new Comedor(); //Usa ciclebarrier.
    private final TrenTuristico trenTuristico; // Usa cola de bloqueo.
    private final RealidadVirtual realidadVirtual = new RealidadVirtual(5, 10, 5); // 5 Equipos completos.
    private final JuegoPremios juegoPremios = new JuegoPremios(); // Usa exchanger.
    private final BarcoPirata barcoPirata;  // Usa semáforo para asientos y monitor para iniciar viaje.

    // Variables de control de horario.
    private boolean ingresoAbierto = true; // Cierra 18:00
    private boolean actividadesAbiertas = true; // Cierra 19:00
    private boolean parqueAbierto = true; // Cierra 23:00
    private int horaSimulada = 9;

    public Parque() {
        // Pasamos 'this' a las clases que necesitan chequear el cierre del parque
        this.montanaRusa = new MontanaRusa(this);
        this.barcoPirata = new BarcoPirata(this);
        this.trenTuristico = new TrenTuristico(this);
        
        
        // Las que no necesitan la referencia (o no la usan aún) se pueden inicializar normal
        this.autitosChocadores = new AutitosChocadores();
        
    }

    // Métodos Getters.
    public MontanaRusa getMontanaRusa() { return montanaRusa; }
    public AutitosChocadores getAutitosChocadores() { return autitosChocadores; }
    public Comedor getComedor() { return comedor; }
    public TrenTuristico getTrenTuristico() { return trenTuristico; }
    public RealidadVirtual getRealidadVirtual() { return realidadVirtual; }
    public JuegoPremios getJuegoPremios() { return juegoPremios; }
    public BarcoPirata getBarcoPirata() { return barcoPirata; }

    public boolean estanActividadesAbiertas() { return actividadesAbiertas; }
    public boolean isIngresoAbierto() { return ingresoAbierto; }
    public boolean isParqueAbierto() { return parqueAbierto; }
    public int getHoraSimulada() { return horaSimulada; }
    

    // Control de Horarios.
    @Override
    public void run() {
        // ... Lógica de horaSimulada, cierre a las 18, 19 y 23.
        try {
            while (parqueAbierto) {
                Thread.sleep(5000); // 5 segundo real = 1 hora simulada.
                horaSimulada++;

                if (horaSimulada == 18) { ingresoAbierto = false; System.out.println("¡ATENCIÓN! El ingreso al parque ha CERRADO (18:00 hrs)."); }
                if (horaSimulada == 19) { actividadesAbiertas = false; System.out.println("¡ATENCIÓN! Todas las actividades han CERRADO (19:00 hrs)."); }
                if (horaSimulada >= 23) { parqueAbierto = false; System.out.println("¡FIN DE JORNADA! El parque CIERRA completamente (23:00 hrs)."); }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}