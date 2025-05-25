package socket;


import aircraft.Aircraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerControl implements Runnable {
    private final Aircraft aircraft;

    public ServerControl(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket;
            serverSocket = new ServerSocket(6666);
            Socket socket = serverSocket.accept();
            while (aircraft != null) {
                final DataInputStream dis = new DataInputStream(socket.getInputStream());
                final String order = dis.readUTF();
                handelOrder(order);
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handelOrder(String order) {
        System.out.println("order = " + order);

        if ("rollRight".contains(order)) {
            aircraft.rollRight();
        } else if ("rollLeft".contains(order)) {
            aircraft.rollLeft();
        } else if ("speedDown".contains(order)) {
            aircraft.speedDown();
        } else if ("speedUp".contains(order)) {
            aircraft.speedUp();
        } else {
            System.out.println("Unexpected value");
        }
    }
}
