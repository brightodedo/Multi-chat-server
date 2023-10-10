package NetworksAss.part5;

import java.io.*;
import java.net.Socket;

public class tcp_client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 3031);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter your name (Please enter your name to join the chat): ");
            String username = reader.readLine();
            out.println(username);

            Thread serverListener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();

            String message;
            while ((message = reader.readLine()) != null) {
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
