public class ControlRemotoVoz extends ControlRemoto {
    public ControlRemotoVoz(Dispositivo dispositivo) {
        super(dispositivo);
    }

    @Override
    public void togglePower() {
        System.out.println("Control de Voz: Alternar encendido");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    @Override
    public void volumeDown() {
        dispositivo.setVolume(dispositivo.getVolume() - 10);
    }

    @Override
    public void volumeUp() {
        dispositivo.setVolume(dispositivo.getVolume() + 10);
    }

    @Override
    public void channelDown() {
        dispositivo.setChannel(dispositivo.getChannel() - 1);
    }

    @Override
    public void channelUp() {
        dispositivo.setChannel(dispositivo.getChannel() + 1);
    }

    /** Interpreta un comando de voz y ejecuta la operacion correspondiente. */
    public void escuchar(String comando) {
        String texto = comando == null ? "" : comando.trim().toLowerCase();
        System.out.println("Control de Voz: escuché \"" + texto + "\"");
        switch (texto) {
            case "encender":
                if (!dispositivo.isEnabled()) {
                    dispositivo.enable();
                }
                break;
            case "apagar":
                if (dispositivo.isEnabled()) {
                    dispositivo.disable();
                }
                break;
            case "subir volumen":
                volumeUp();
                break;
            case "bajar volumen":
                volumeDown();
                break;
            case "canal siguiente":
                channelUp();
                break;
            case "canal anterior":
                channelDown();
                break;
            case "silenciar":
                dispositivo.setVolume(0);
                break;
            default:
                System.out.println("Control de Voz: no entendí el comando");
        }
    }
}
