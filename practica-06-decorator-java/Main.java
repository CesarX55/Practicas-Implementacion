import java.util.List;

public class Main {
    public static void main(String[] args) {
        Usuario ana = new Usuario("Ana");
        ana.activarSuscripcion(ServicioStreaming.DISNEY);
        ana.activarSuscripcion(ServicioStreaming.WARNER);

        // Cada catalogo se envuelve una sola vez con el decorador de suscripcion.
        CatalogoPeliculas disney = new SuscripcionDecorator(new CatalogoDisney(), ana);
        CatalogoPeliculas paramount = new SuscripcionDecorator(new CatalogoParamount(), ana);
        CatalogoPeliculas warner = new SuscripcionDecorator(new CatalogoWarner(), ana);

        System.out.println("=== Ana tiene Disney y Warner ===");
        consultar(disney);
        consultar(warner);
        consultar(paramount);

        System.out.println("\n=== Ana activa Paramount ===");
        ana.activarSuscripcion(ServicioStreaming.PARAMOUNT);
        consultar(paramount);

        System.out.println("\n=== Ana cancela Disney ===");
        ana.cancelarSuscripcion(ServicioStreaming.DISNEY);
        consultar(disney);
    }

    private static void consultar(CatalogoPeliculas catalogo) {
        String servicio = catalogo.getServicio().getNombre();
        try {
            List<String> peliculas = catalogo.obtenerPeliculas();
            System.out.println("[ACCESO PERMITIDO] " + servicio + ": " + peliculas);
        } catch (IllegalStateException e) {
            System.out.println("[ACCESO DENEGADO] " + e.getMessage());
        }
    }
}
