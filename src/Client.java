/**
 * Created by petro on 21-May-17.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {

    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Establish connection with a server and create streams for communication
        System.out.println("Enter IP Address of the Server: ");
        String serverAddress = getClientInput();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        String clientName;

        // Process replies from server
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                System.out.println("Enter a client name: ");
                clientName = getClientInput();
                out.println(clientName);
            }
            else if (line.startsWith("WELCOME")) {
                System.out.println(line);
                String tempLine = "";
                while (!tempLine.startsWith("5")){
                    tempLine = in.readLine();
                    System.out.println(tempLine);
                }
            }
            else if (line.startsWith("COMMAND")) {
                System.out.println("Enter a command: ");
                out.println(getClientInput());
            }
            else if(line.startsWith("SERVER")) {
                String answer = in.readLine();
                System.out.println(line + ": " + answer);
                //Close after client calls exit and server closes the client thread.
                if(answer.equals("EXIT")) {
                    System.out.println("Session closed.");
                    break;
                }
                else {
                    System.out.println("Enter a command: ");
                    out.println(getClientInput());
                }
            }
        }
    }

    /**
     * Get input from the client
     */
    private String getClientInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        try {
            input = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

}