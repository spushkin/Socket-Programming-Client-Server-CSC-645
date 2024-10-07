import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TextClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                Scanner scanner = new Scanner(System.in);
        ) {
            boolean isRunning = true;
            String option;

            while (isRunning) {
                // Display menu
                System.out.println("Menu:");

                option = scanner.nextLine();
                writer.println(option);

                if (option.equals("4")) {
                    System.out.println("Exiting...");
                    isRunning = false;
                } else {
                    handleServerResponse(reader, scanner, writer, option);
                }
            }
        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleServerResponse(BufferedReader reader, Scanner scanner, PrintWriter writer, String option) throws IOException {
        String serverResponse;

        switch (option) {
            case "0": // Login
                System.out.println("Connecting to the server...");
                while ((serverResponse = reader.readLine()) != null) {
                    System.out.println("Server: " + serverResponse);

                    if (serverResponse.equals("Please enter your username:")) {
                        System.out.print("Enter username: ");
                        writer.println(scanner.nextLine().trim());
                    } else if (serverResponse.equals("Please enter your password:")) {
                        System.out.print("Enter password: ");
                        writer.println(scanner.nextLine().trim());
                    }
                }
                break;
            case "1": // Get user list
                System.out.println("Fetching");
                while ((serverResponse = reader.readLine()) != null) {
                    if (serverResponse.isEmpty()) {
                        break; // Exit the loop after receiving the full list
                    }
                    System.out.println(serverResponse);
                }
                break;
            case "2": // Send message
                while ((serverResponse = reader.readLine()) != null) {
                    System.out.println("Server: " + serverResponse);
                    if (serverResponse.equals("Enter the recipient's username:")) {
                        System.out.print("Recipient: ");
                        String recipient = scanner.nextLine().trim();
                        writer.println(recipient);
                    } else if (serverResponse.equals("Enter your message:")) {
                        System.out.print("Message: ");
                        String message = scanner.nextLine().trim();
                        writer.println(message);
                    } else if (serverResponse.contains("Message successfully sent") || serverResponse.contains("User not found")) {
                        System.out.println("Server: " + serverResponse);
                        break; // Exit the loop after the message is processed
                    }
                }
                break;
            case "3": // Get my messages
                System.out.println("Fetching messages...");
                while ((serverResponse = reader.readLine()) != null) {
                    if (serverResponse.isEmpty()) {
                        break; // Exit the loop after receiving all messages
                    }
                    System.out.println(serverResponse);
                }
                System.out.println("End of messages.");
                break;
            default:
                System.out.println("Invalid option");
                break;
        }
    }
}
