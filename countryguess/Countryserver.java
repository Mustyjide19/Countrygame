package countryguess;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;

public class Countryserver {
    private static final String EXIT_COMMAND = "exit";
    private static boolean initialized = false;
    private static String clientName = "";

    public static void main(String[] args) {
        int portNumber = 17777;

        try {
            SSLServerSocketFactory sslServerSocketFactory = createSSLServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(portNumber);

            System.out.println("Waiting for connections...");

            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            if (sslSocket.isConnected())
                System.out.println("Connected: " + sslSocket.getInetAddress() + ":" + sslSocket.getPort());

            // Use BufferedWriter for efficient writing
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            // Initialize
            String welcomeMessage = "Welcome to the Country Guessing Game! Guess the capital of the given country.";
            out.write(welcomeMessage);
            out.newLine(); // Add newline to indicate the end of the message
            out.flush(); // Flush the buffer to ensure the message is sent

            // Interaction loop
            while (true) {
                // Generate a random country for each round
                String currentCountry = getRandomCountry();
                out.write(currentCountry);
                out.newLine();
                out.flush();

                // Receive the client's guess
                String userGuess = in.readLine();

                if (userGuess == null) {
                    // Connection closed by the client
                    break;
                }

                // Check for exit command
                if (userGuess.equalsIgnoreCase(EXIT_COMMAND)) {
                    System.out.println("Terminating...");
                    sslSocket.close();
                    sslServerSocket.close();
                    System.exit(0);
                }

                // Provide feedback to the client
                String feedback = processGuess(currentCountry, userGuess);
                out.write(feedback);
                out.newLine();
                out.flush();
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static SSLServerSocketFactory createSSLServerSocketFactory()
            throws IOException, GeneralSecurityException {
        char[] keystorePassword = "Olaitan29".toCharArray(); // Change to your keystore password

        try {
            String keystoreFilePath = "C:\\Users\\musty\\OneDrive\\New workspace\\countryguess\\resources\\countryserver_keystore.jks";
            KeyStore keyStore = KeyStore.getInstance("PKCS12"); // Change to PKCS12
            keyStore.load(new FileInputStream(keystoreFilePath), keystorePassword);

            // Specify the correct alias for your server certificate
            String alias = "country"; // Change this to your actual alias

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keystorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            return sslContext.getServerSocketFactory();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception after printing the stack trace
        }
    }

    private static String getRandomCountry() {
        // Replace this with your list of countries
        String[] countries = {"France", "Germany", "Japan", "Canada", "Brazil", "United Kingdom", "Italy", "Australia", "India", "China", "Mexico"};
        int index = (int) (Math.random() * countries.length);
        return countries[index];
    }

    private static String processGuess(String country, String guess) {
        // Replace this with your logic to check the correctness of the guess
        if (guess.trim().equalsIgnoreCase(getCapital(country).toLowerCase())) {
            return "Correct! The capital of " + country + " is " + getCapital(country);
        } else {
            return "Incorrect. The correct capital of " + country + " is " + getCapital(country) + ". Try again!";
        }
    }

    private static String getCapital(String country) {
        // Replace this with your map of countries to capitals
        switch (country.toLowerCase()) {
            case "france":
                return "Paris";
            case "germany":
                return "Berlin";
            case "japan":
                return "Tokyo";
            case "canada":
                return "Ottawa";
            case "brazil":
                return "Brasília";
            case "united kingdom":
                return "London";
            case "italy":
                return "Rome";
            case "australia":
                return "Canberra";
            case "india":
                return "New Delhi";
            case "china":
                return "Beijing";
            case "mexico":
                return "Mexico City";
            default:
                return "Unknown Capital";
        }
    }
}
