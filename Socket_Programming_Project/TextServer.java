import java.io.*;
import java.net.*;
import java.util.*;

public class TextServer {
    private static final int PORT = 12345;
    private static final Map<String, String> userCredentials = new HashMap<>();
    private static final Map<String, List<String>> userMessages = new HashMap<>();

    public static void main(String[] args) {
        // Initialize user accounts
        userCredentials.put("Alice", "1234");
        userCredentials.put("Bob", "5678");
        userMessages.put("Alice", new ArrayList<>());
        userMessages.put("Bob", new ArrayList<>());

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());

                // Create a new thread for each client connection
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
            ) {
                String username = null;
                boolean isConnected = true;

                while (isConnected) {
                    // Read option from client
                    String option = reader.readLine();
                    System.out.println("Received option: " + option);

                    switch (option) {
                        case "0":
                            // Handle login
                            username = handleLogin(reader, writer);
                            break;
                        case "1":
                            // Send user list
                            sendUserList(writer);
                            break;
                        case "2":
                            // Send message
                            handleSendMessage(reader, writer, username);
                            break;
                        case "3":
                            // Retrieve messages
                            sendUserMessages(writer, username);
                            break;
                        case "4":
                            // Exit
                            writer.println("Goodbye!");
                            isConnected = false;
                            break;
                        default:
                            writer.println("Invalid option");
                    }
                }
            } catch (IOException e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close socket: " + e.getMessage());
                }
            }
        }

        private String handleLogin(BufferedReader reader, PrintWriter writer) throws IOException {
            while (true) {
                writer.println("Please enter your username:");
                String username = reader.readLine().trim();
                System.out.println("Login attempt with username: " + username);

                writer.println("Please enter your password:");
                String password = reader.readLine().trim();
                System.out.println("Login attempt with password: " + password);

                synchronized (userCredentials) {
                    if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                        writer.println("Access Granted. Welcome, " + username + "!");
                        return username;
                    } else {
                        writer.println("Access Denied – Username/Password Incorrect");
                    }
                }
            }
        }

        private void sendUserList(PrintWriter writer) {
            synchronized (userCredentials) {
                writer.println("User List:");
                for (String user : userCredentials.keySet()) {
                    writer.println("- " + user);
                }
                writer.println();
            }
        }

        private void handleSendMessage(BufferedReader reader, PrintWriter writer, String sender) throws IOException {
            if (sender == null) {
                writer.println("Message failed: You must be logged in to send messages.");
                return;
            }

            writer.println("Enter the recipient's username:");
            String recipient = reader.readLine().trim();
            System.out.println("Received recipient: " + recipient);

            writer.println("Enter your message:");
            String message = reader.readLine().trim();
            System.out.println("Received message: " + message);

            if (message == null || message.isEmpty()) {
                writer.println("Message failed: Invalid message content.");
                return;
            }

            synchronized (userMessages) {
                if (userMessages.containsKey(recipient)) {
                    userMessages.get(recipient).add("From " + sender + ": " + message);
                    writer.println("Message successfully sent to " + recipient + ".");
                    System.out.println("Message sent from " + sender + " to " + recipient + ": " + message);
                } else {
                    writer.println("User not found: " + recipient + " does not exist.");
                }
            }
        }

        private void sendUserMessages(PrintWriter writer, String username) {
            synchronized (userMessages) {
                List<String> messages = userMessages.get(username);
                if (messages != null && !messages.isEmpty()) {
                    writer.println("Your Messages:");
                    for (String message : messages) {
                        writer.println(message);
                    }
                    writer.println(); // Send an empty line to indicate the end of the messages
                } else {
                    writer.println("You have no new messages.");
                    writer.println(); // Ensure an empty line is sent to signify end of response
                }
            }
        }

    }
}
