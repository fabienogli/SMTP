package common;

import java.io.*;

public class StreamHandling {

    public static void write(String message, OutputStream outputStream) {
        PrintWriter outToClient = new PrintWriter(outputStream);
        //outToClient.write(message);
        System.out.println("envoie " + message);
        String[] resultTable=message.split("\n");
        if(resultTable.length>1){
            for(int i=0;i<resultTable.length;i++){
                outToClient.write(resultTable[i] + "\n");
                outToClient.flush();
            }
        }else{
            outToClient.write(message + "\n");
            outToClient.flush();
        }
    }

    public static void writeMutlipleLines(String message, OutputStream outputStream) {
        PrintWriter outToClient = new PrintWriter(outputStream);
        outToClient.write(message);
        System.out.println("le serveur envoie" + message);
        outToClient.println();
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

   /* public static String readMultipleLines(InputStream inputStream) {
        StringBuilder data = new StringBuilder();
        try {
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(inputStream));
            String tmp = fromServer.readLine();
            do {
                data.append(tmp).append("\n");
                tmp = fromServer.readLine();
                if (tmp.length() == 0) {
                    data.append(tmp).append("\n");
                    tmp = fromServer.readLine();
                }
            } while (tmp.charAt(0) != '.');
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Plusieurs ligne, reception " + data.toString());
        return data.toString();
    }*/
   public static  String readMultipleLines(InputStream inputStream) {
       StringBuilder result = new StringBuilder();
       String s;
       try {
           BufferedReader fromClient = new BufferedReader(new InputStreamReader(inputStream));
           do{
               s = fromClient.readLine();
               result.append(s).append("\n");
           }
           while (!s.equals("."));
       } catch (IOException e) {
           e.printStackTrace();
       }
       return result.toString();
   }
}