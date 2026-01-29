package UDP;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClienteHandler implements Runnable {

    public static ArrayList<ClienteHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreCliente;

    public ClienteHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.nombreCliente = bufferedReader.readLine();
            clientHandlers.add(this);

            mandarMensaje("SERVER: " + nombreCliente + " ha entrado al chat!");
        } catch (IOException e) {
            cerrarComunicacion(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String mensajeDesdeCliente;
        while (socket.isConnected()) {
            try {
                mensajeDesdeCliente = bufferedReader.readLine();
                if (mensajeDesdeCliente == null) break;
                mandarMensaje(mensajeDesdeCliente);
            } catch (IOException e) {
                cerrarComunicacion(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    public void mandarMensaje(String mensaje) {
        for (ClienteHandler clienteHandler : clientHandlers) {
            try {

                if (!clienteHandler.nombreCliente.equals(nombreCliente)) {
                    clienteHandler.bufferedWriter.write(mensaje);
                    clienteHandler.bufferedWriter.newLine();
                    clienteHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                cerrarComunicacion(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public void quitarClienteHandler() {
        clientHandlers.remove(this);
        mandarMensaje("SERVER: " + nombreCliente + " ha salido.");
    }

    public void cerrarComunicacion(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        quitarClienteHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}