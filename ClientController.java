package socket;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientController {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Socket socket = new Socket("localhost", 6666);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String order = "";
            while (!order.equals("stop")) {
                order = sc.nextLine();
                dataOutputStream.writeUTF(order);
                dataOutputStream.flush();
            }
            dataOutputStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}