package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente {
    private DatagramSocket socket;
    private InetAddress address;
    private int puerto;
    private String nombreUsuario;

    public Cliente(String host, int puerto, String nombreUsuario) throws IOException {
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(host);
        this.puerto = puerto;
        this.nombreUsuario = nombreUsuario;
    }

    public void enviarMensaje() {
        try {
            Scanner scanner = new Scanner(System.in);
            enviar(nombreUsuario + " ha entrado al chat!");

            while (true) {
                String mensaje = scanner.nextLine();
                if (mensaje.equalsIgnoreCase("salir")) break;
                enviar(nombreUsuario + ": " + mensaje);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviar(String msg) throws IOException {
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, puerto);
        socket.send(packet);
    }

    public void escucharMensaje() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (!socket.isClosed()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    System.out.println(new String(packet.getData(), 0, packet.getLength()));
                } catch (IOException e) {
                    if (!socket.isClosed()) e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu nombre de usuario: ");
        String nombre = scanner.nextLine();

        Cliente cliente = new Cliente("localhost", 5000, nombre);
        cliente.escucharMensaje();
        cliente.enviarMensaje();
    }
}