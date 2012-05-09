package jp.ddo.kingdragon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class USBHostTest extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextArea receiveMessage;
    private JTextField sendMessage;
    private JButton sendButton;

    private BufferedReader receiveStream;
    private PrintWriter pw;

    public USBHostTest() {
        Socket mSocket;
        try {
            mSocket = new Socket("127.0.0.1", 8080);
            receiveStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            pw = new PrintWriter(mSocket.getOutputStream());
            new Thread(new ReceiveMessageThread()).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLayout(new BorderLayout());

                // 各コンポーネントの設定
                receiveMessage = new JTextArea();
                receiveMessage.setLineWrap(true);
                receiveMessage.setWrapStyleWord(true);
                receiveMessage.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(receiveMessage);
                add(scrollPane, BorderLayout.CENTER);

                JPanel panel = new JPanel(new BorderLayout());
                sendMessage = new JTextField();
                sendMessage.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {}

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                            sendButton.doClick();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {}
                });
                panel.add(sendMessage, BorderLayout.CENTER);
                sendButton = new JButton("送信");
                sendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String message = sendMessage.getText();
                        if(message.length() > 0) {
                            try {
                                pw.println(message);
                                pw.flush();
                                receiveMessage.append("me : " + message + "\n");
                                sendMessage.setText("");
                            }
                            catch(Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
                panel.add(sendButton, BorderLayout.EAST);
                add(panel, BorderLayout.SOUTH);

                // フレームの設定
                setTitle("USBHostTest");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(600, 400);
                setVisible(true);
            }
        });
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
                    final String temp = message;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            receiveMessage.append("Phone : " + temp + "\n");
                        }
                    });
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}