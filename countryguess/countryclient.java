package countryguess;

import javax.net.ssl.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class countryclient {
    private static final String EXIT_COMMAND = "exit";

    public static void main(String[] args) throws IOException {

        int portNumber = 17777;
        String hostName = "127.0.0.1", clientName = "Gamer";

        System.out.println("Connecting...");
        try {
            char[] keystorePassword = "Olaitan29".toCharArray();
            char[] keyPassword = "Olaitan29".toCharArray();
            String keystoreFilePath = "C:\\Users\\musty\\OneDrive\\New workspace\\countryguess\\resources\\countryserver_keystore.jks";

            SSLSocketFactory sslSocketFactory = createSSLSocketFactory(keystoreFilePath, keystorePassword, keyPassword);
            SSLSocket s = (SSLSocket) sslSocketFactory.createSocket(hostName, portNumber);

            if (s.isConnected())
                System.out.println("Connected to " + s.getInetAddress() + ":" + s.getPort() + " from " + s.getLocalAddress() + ":" + s.getLocalPort());

            // Use BufferedWriter for efficient writing
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            // Initialize
            System.out.println("Initializing... (" + clientName + ")");
            System.out.println("HELLO" + " " + clientName);
            out.newLine(); // Add newline to indicate the end of the message
            out.flush(); // Flush the buffer to ensure the message is sent

            String welcomeMessage = in.readLine();
            System.out.println(welcomeMessage);

            if (!welcomeMessage.trim().equalsIgnoreCase("Welcome to the Country Guessing Game! Guess the capital of the given country.")) {
                System.out.println("Server rejected.");
                s.close();
                System.exit(0);
            }

            // Interaction loop
            while (true) {
                // Receive the country from the server
                String countryPrompt = in.readLine();
                System.out.println("Guess the capital of " + countryPrompt);

                // Ask the client for a guess
                System.out.print("Enter the capital (or type 'exit' to end): ");
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                String userGuess = userInput.readLine();

                //.String userGuess = userInput.readLine();

                // Sending the user's guess to the server
                out.write(userGuess);
                out.newLine();
                out.flush();

                // Check for exit command
                if (userGuess.equalsIgnoreCase(EXIT_COMMAND)) {
                    System.out.println("Terminating");
                    s.close();
                    System.exit(0);
                }

                // Receive feedback from the server
                String feedback = in.readLine();
                System.out.println(feedback);
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static SSLSocketFactory createSSLSocketFactory(String keystoreFilePath, char[] keystorePassword, char[] keyPassword)
            throws IOException, GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keystoreFilePath), keystorePassword);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyPassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
