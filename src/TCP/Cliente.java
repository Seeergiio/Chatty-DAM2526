package TCP;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreUsuario;

    public Cliente(Socket socket, String nombreUsuario) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.nombreUsuario = nombreUsuario;
        } catch (IOException e) {
            cerrarComunicacion(socket, bufferedWriter, bufferedReader);
        }
    }

    public void enviarMensaje() {
        try {
            bufferedWriter.write(nombreUsuario);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String mensaje = scanner.nextLine();
                bufferedWriter.write(nombreUsuario + ": " + mensaje);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            cerrarComunicacion(socket, bufferedWriter, bufferedReader);
        }
    }

    public void escucharMensaje() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgDelChat;
                while (socket.isConnected()) {
                    try {
                        msgDelChat = bufferedReader.readLine();
                        if (msgDelChat != null) {
                            System.out.println(msgDelChat);
                        }
                    } catch (IOException e) {
                        cerrarComunicacion(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }

    public void cerrarComunicacion(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu nombre de usuario: ");
        String nombreCliente = scanner.nextLine();

        Socket socket = new Socket("localhost", 5000);
        Cliente cliente = new Cliente(socket, nombreCliente);

        cliente.escucharMensaje();
        cliente.enviarMensaje();
    }
}