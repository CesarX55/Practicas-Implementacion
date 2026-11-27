/** Version original, sin Proxy: la carga ocurre al crear el objeto. */
public class MainSinProxy {
    public static void main(String[] args) {
        System.out.println("Application started.");

        Image image = new RealImage("photo.jpg");
        System.out.println("RealImage created (already loaded).");

        image.display();

        System.out.println("Second call:");
        image.display();
    }
}
