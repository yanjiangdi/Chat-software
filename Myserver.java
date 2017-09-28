package yjd9;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Myserver extends JFrame implements ActionListener, Runnable {
    JTextArea jTextReceive = new JTextArea();
    JTextField jTextSend = new JTextField();
    JButton jbs = new JButton("����");
    ServerSocket server = null;
    JScrollPane jsp = new JScrollPane(jTextReceive);
    Socket clientSocket;     //����ǰ�߳���C/Sͨ���е�Socket����
    boolean flag = true;       //����Ƿ����
    Thread connenThread;     //��ͻ��˷�����Ϣ���߳�
    BufferedReader sin;      //����������
    DataInputStream sin1;
    DataOutputStream sout;   //���������
    boolean flags = false;

    public static void main(String[] args) {
        Myserver MS = new Myserver();
        MS.serverStart();
    }
    public synchronized void changeFlag(boolean t){
       flags=t; 
    }
            

    public void serverStart() {
        try {
            server = new ServerSocket(8080);    //������������
            this.setTitle("�������ˣ��˿ں�:" + server.getLocalPort());
            this.setLayout(null);
            this.setBounds(20, 00, 300, 300);
            jsp.setBounds(20, 20, 220, 100);
            jTextSend.setBounds(20, 120, 220, 50);
            jbs.setBounds(20, 170, 220, 30);
            this.add(jsp);
            this.add(jTextSend);
            this.add(jbs);
            this.setVisible(true);
            jbs.addActionListener(this);
            while (flag) {
                clientSocket = server.accept();
                jTextReceive.setText("�����Ѿ��������!\n");
                try (InputStream is = clientSocket.getInputStream()) {
                    sin = new BufferedReader(new InputStreamReader(is));
                    try (OutputStream os = clientSocket.getOutputStream()) {
                        sin1 = new DataInputStream(clientSocket.getInputStream());
                        sout = new DataOutputStream(os);
                        connenThread = new Thread(this);
                        connenThread.start();     //�����̣߳���ͻ��˷�����Ϣ
                        String aLine;
                        while(true){
                            aLine=sin1.readUTF();
                            jTextReceive.append("�ͻ��˷�����Ϣ��" +aLine+"\n");
                            if (aLine.equals("bye")) {
                                flag = false;
                                connenThread.interrupt();
                                break;
                            }
                        }
                        sout.close();
                    }
                    sin.close();
                }
                clientSocket.close();	 //�ر�Socket����
                System.exit(0);          //�������н��� 
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Myserver.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                    if (flags == true) {
                    changeFlag(false);
                    String str = jTextSend.getText().trim();
                   if(str==null||str.length()<=0){}
                    else{
                    sout.writeUTF(str);
                    sout.flush();
                    jTextReceive.append("������������Ϣ:"+str+"\n");   }            
                    jTextSend.setText("");
                    }

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void finalize() //��������
    {
        try {
            server.close();
        } //ֹͣServerSocket����
        catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton jbt = (JButton) e.getSource();
        if (jbt == jbs) //������ȷ�϶Ի����еġ����͡���ť
        {
            changeFlag(true);
        }
    }
}



