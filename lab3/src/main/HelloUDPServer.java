package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;


public class HelloUDPServer {

    private final static int BUFFER_SIZE = 8192;
    private static int THREADS;
    private final static String RESPONSE_PREFIX = "Hello, ";
    private final int port;



    public HelloUDPServer(int port, int threadNumber) {
        this.port = port;
        THREADS = threadNumber;
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int threadNumber = Integer.parseInt(args[1]);

        System.out.println(LocalTime.now()  + " Port: " + port + " Thread number: " + threadNumber);
        new HelloUDPServer(port, threadNumber).start();
    }

    public void start() {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(port);
            serverSocket.setReceiveBufferSize(BUFFER_SIZE);
            serverSocket.setSendBufferSize(BUFFER_SIZE);
            for (int i = 0; i < THREADS; i++) {
                new Thread(new Receiver(serverSocket)).start();
            }
            while (true){

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private class Receiver implements Runnable {

        private final DatagramSocket socket;

        public Receiver(DatagramSocket serverSocket) {
            socket = serverSocket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    socket.receive(packet);
                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(LocalTime.now() + " Received: " + receivedMessage);

                    String response = RESPONSE_PREFIX + receivedMessage;
                    packet.setData(response.concat("\r\n").getBytes());
                    socket.send(packet);

                    System.out.println(LocalTime.now()  + " Sent: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}