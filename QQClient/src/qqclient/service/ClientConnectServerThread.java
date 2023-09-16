package qqclient.service;

import comment.Message;
import comment.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/13 17:44
 * 使用线程来实现Socket实时接收服务端的信息
 */
public class ClientConnectServerThread extends Thread{
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //持续接收来自服务器的信息
        while(true){
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果没有Message对象，会阻塞在这里
                Message message = (Message) ois.readObject();
                //对message判断类型，并做出相应的处理
                if(message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    String[] s = message.getContent().split(" ");
                    System.out.println("=============在线用户列表============");
                    for(int i = 0 ; i < s.length ; i++){
                        System.out.println("用户：" + s[i]);
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_PRIVATE_INF)) {
                    System.out.println("=============私聊信息============");
                    //显示私聊信息
                    System.out.println(message.getSender() + " 对 " + message.getGetter() + " 说 ：" + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_ALL_MES)) {
                    //接收群发信息，并显示在控制台
                    System.out.println("=============群发信息============");
                    System.out.println(message.getSender() + " 对 所有人 说：" + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_PRIVATE_FILE)) {
                    //接收私法文件，并显示在控制台
                    System.out.println("=============私发文件============");
                    System.out.println("发送者：" + message.getSender() + "  发送文件地址：" + message.getSrc());
                    System.out.println("接收位置：" + message.getDest());
                    FileOutputStream fos = new FileOutputStream(message.getDest());
                    fos.write(message.getBuf(),0,message.getBuf().length);

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL)) {
                    //接收服务器推发信息，并显示在控制台
                    System.out.println("服务器 ：" + message.getContent());

                } else {
                    System.out.println("其他类型暂不处理...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
