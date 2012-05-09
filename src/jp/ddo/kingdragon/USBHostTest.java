package jp.ddo.kingdragon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class USBHostTest {
    private BufferedReader receiveStream;
    private BufferedReader sendStream;
    private PrintWriter pw;

    public USBHostTest() {
        Socket mSocket;
        try {
            mSocket = new Socket("127.0.0.1", 8080);
            receiveStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            sendStream = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            pw = new PrintWriter(mSocket.getOutputStream());
            new Thread(new ReceiveMessageThread()).start();
            new Thread(new SendMessageThread()).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new USBHostTest();
    }

    class ReceiveMessageThread implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while((message = receiveStream.readLine()) != null) {
                    System.out.println("Phone : " + message);
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    String line = sendStream.readLine();
                    if(line.length() > 0) {
                        pw.println(line);
                        pw.flush();
                        System.out.println("me : " + line);
                    }
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}