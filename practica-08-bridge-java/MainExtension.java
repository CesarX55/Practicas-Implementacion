public class MainExtension {
    public static void main(String[] args) {
        // Nuevo dispositivo: el Projector funciona con los controles existentes
        System.out.println("=== Control Básico con Projector ===");
        ControlRemoto control1 = new ControlRemotoBasico(new Projector());
        Main.probarControlRemoto(control1);

        System.out.println("\n=== Control Avanzado con Projector ===");
        ControlRemotoAvanzado control2 = new ControlRemotoAvanzado(new Projector());
        Main.probarControlRemoto(control2);
        control2.record();

        // Nuevo control: ControlRemotoVoz con un dispositivo ya existente
        System.out.println("\n=== Control de Voz con TV ===");
        ControlRemotoVoz voz = new ControlRemotoVoz(new TV());
        voz.escuchar("encender");
        voz.escuchar("subir volumen");
        voz.escuchar("canal siguiente");
        voz.escuchar("silenciar");
        voz.escuchar("apagar");
        voz.escuchar("bailar");

        // Cambio de dispositivo en tiempo de ejecución
        System.out.println("\n=== Cambio de dispositivo en tiempo de ejecución ===");
        ControlRemoto control = new ControlRemotoAvanzado(new Radio());
        control.togglePower();
        control.setDispositivo(new TV());
        control.togglePower();
        control.setDispositivo(new Projector());
        control.togglePower();
    }
}
