import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

/**
 * A multithreaded server. Supports multiple clients.
 */
public class Server {

    // Port used for the server socket.
    private static final int PORT = 9001;

    // The set of all names of clients connected
    private static HashSet<String> names = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Server is running.");
        ServerSocket serverSocket = new ServerSocket(PORT);
        try {
            // Support multiple clients. Accept new connections and
            // start a new thread for each client to service them
            while (true) {
                new Worker(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    /**
     * A worker thread class. Deals with each client separately
     * and executes commands.
     */
    private static class Worker extends Thread {
        private String name;
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public Worker(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * Requests a unique client name. Gets inputs and
         * executes commands. Broadcasts responses to each client.
         */
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("connection established");

                // Get a unique name that isn't in use from the client.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                out.println("WELCOME, " + name);
                out.println(help());

                // Accept and execute commands
                out.println("COMMAND");
                while (true) {
                    String input = in.readLine();
                    out.println("SERVER");

                    if(input.equals("d")) {
                        out.println(d());
                        }
                    else if(input.equals("t")) {
                        out.println(t());
                    }
                    else if(input.equals("h")) {
                        out.println(h());
                    }
                    else if(input.startsWith("m")) {
                        out.println(m(input.split(" ", 2)[1]));
                    }

                    else if(input.startsWith("e")) {
                        out.println("EXIT");
                        e();
                    }
                    else {
                        out.println("INVALID COMMAND");
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Remove the client. Delete its name, print writer and close the socket.
                if (name != null) {
                    names.remove(name);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                }
            }
        }

        private String d() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            return dateFormat.format(date);
        }

        private String t() {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            return dateFormat.format(date);
        }

        private String h() {
            return "Hello, " + name;
        }

        private String m(String str){
            return str;
        }

        private String help() {
            String info = "You are connected to the server.\n" +
                    "This is a list of possible commands: \n" +
                    "1. d -- returns current date in yyyy/MM/dd format \n" +
                    "2. t -- returns curreint time in HH:mm:ss format \n" +
                    "3. h -- greeting. returns 'Hello, client' \n" +
                    "4. m 'message' -- returns a string following the m command.\n" +
                    "   for example: m 'test' -- returns 'test' \n" +
                    "5. e -- Exit program. Close connection.";
            return info;
        }

        private void e(){
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}