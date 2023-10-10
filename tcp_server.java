package NetworksAss.part5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class tcp_server {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server is started...");
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(3031);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine();
                System.out.println(username + " has joined the chat.");

                synchronized (clientWriters) {
                    for (PrintWriter writer : clientWriters) {
                        writer.println(username + " has joined the chat.");
                    }
                    clientWriters.add(out);
                }

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        return;
                    }
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            if(!writer.equals(this.out)){
                                writer.println(username + ": " + message);
                            }
                        }
                    }
                    System.out.println(username + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    System.out.println(username + " has left the chat.");
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                        for (PrintWriter writer : clientWriters) {
                            writer.println(username + " has left the chat.");
                        }
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
