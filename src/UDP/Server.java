package UDP;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private DatagramSocket socket;
    private byte[] buffer = new byte[1024];
    private Set<String> clientes = new HashSet<>();

    public Server(int puerto) throws IOException {
        this.socket = new DatagramSocket(puerto);
    }

    public void iniciar() {
        System.out.println("Servidor UDP iniciado...");
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String mensaje = new String(packet.getData(), 0, packet.getLength());
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String clienteId = address.getHostAddress() + ":" + port;

                if (!clientes.contains(clienteId)) {
                    clientes.add(clienteId);
                    System.out.println("Nuevo cliente: " + clienteId);
                }


                difundir(mensaje, address, port);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void difundir(String mensaje, InetAddress emisorAddr, int emisorPort) {
        for (String cliente : clientes) {
            try {
                String[] partes = cliente.split(":");
                InetAddress destAddr = InetAddress.getByName(partes[0]);
                int destPort = Integer.parseInt(partes[1]);


                if (!(destAddr.equals(emisorAddr) && destPort == emisorPort)) {
                    byte[] data = mensaje.getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, destAddr, destPort);
                    socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
        server.iniciar();
    }
}