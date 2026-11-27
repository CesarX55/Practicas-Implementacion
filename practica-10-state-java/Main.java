public class Main {
    public static void main(String[] args) {
        System.out.println("=== Flujo normal: pendiente -> procesado -> enviado -> entregado ===");
        Pedido pedido = new Pedido();
        pedido.mostrarEstado();
        pedido.procesar();
        pedido.mostrarEstado();
        pedido.enviar();
        pedido.mostrarEstado();
        pedido.entregar();
        pedido.mostrarEstado();

        System.out.println("\n=== Transiciones inválidas desde pendiente ===");
        Pedido pendiente = new Pedido();
        pendiente.enviar();
        pendiente.entregar();
        pendiente.mostrarEstado();

        System.out.println("\n=== Cancelación de un pedido pendiente ===");
        Pedido cancelado = new Pedido();
        cancelado.cancelar();
        cancelado.mostrarEstado();
        cancelado.procesar();
        cancelado.mostrarEstado();

        System.out.println("\n=== No se puede cancelar un pedido ya procesado ===");
        Pedido procesado = new Pedido();
        procesado.procesar();
        procesado.cancelar();
        procesado.mostrarEstado();

        System.out.println("\n=== Un pedido entregado ya no cambia ===");
        pedido.cancelar();
        pedido.enviar();
        pedido.mostrarEstado();
    }
}
