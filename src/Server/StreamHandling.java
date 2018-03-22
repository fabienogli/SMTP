package Server;

import java.io.*;

public class StreamHandling {
    public StreamHandling() {
    }

    public static void write(String message, OutputStream outputStream) {
        PrintWriter outToClient = new PrintWriter(outputStream);
        outToClient.write(message);
        System.out.println("envoie " + message);
        outToClient.println();
        outToClient.flush();
    }

    public static String read(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(inputStream));
            result = fromClient.readLine();
            System.out.println("reception " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String readMultipleLines(InputStream inputStream) {
        StringBuilder data = new StringBuilder();
        try {
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(inputStream));
            String tmp = fromServer.readLine();
            do {
                data.append(tmp).append("\n");
                tmp = fromServer.readLine();
            } while (tmp.length() > 0 && tmp.charAt(0) != '.');
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Plusieurs ligne, reception " + data.toString());
        return data.toString();
    }
}