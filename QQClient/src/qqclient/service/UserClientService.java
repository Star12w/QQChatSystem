package qqclient.service;

import comment.Message;
import comment.MessageType;
import comment.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/13 16:59
 * 目的：客户端用户操作
 */
public class UserClientService {
    private User u = new User();


    //判断用户名和密码是否正确，返回true/false
    public boolean Check(String Id, String pwd){
        boolean b = false;
        u.setName(Id);
        u.setPasswd(pwd);
        try {
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"),9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送给服务器User，用于验证

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();//接收来自服务器发来的验证信息

            //处理信息
            if(message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){//验证成功，登录系统
                ClientConnectServerThread ccst = new ClientConnectServerThread(socket);
                ccst.start();
                ManageClientConnectServerThread.addThread(Id,ccst);
                b = true;
            }else {
                //登录失败，不能启动线程，关闭Socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //方法：向服务器发送请求返回在线用户列表
    public void OnlineFriendList(){
        //发送的message的类型是MESSAGE_GET_ONLINE_FRIEND，要求返回在线用户列表
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getName());

        //使用该线程的socket
        //从线程集合拿到相应的线程，在从线程中拿到socket，在拿出OutputStream流
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.get(u.getName()).getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方法：使得客户端正常退出，并给服务端发信息使得相应的线程也退出
    public void logout(){
        //message,类型是6，退出程序
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getName());

        //发送message
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.get(u.getName()).getSocket().getOutputStream());
            oos.writeObject(message);

            //关闭客户端程序
            System.out.println(u.getName() + "用户退出！");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方法：发送私聊信息
    public void PrivateChat(String useId ,String content){
        //编辑message信息
        Message message = new Message();
        message.setSender(u.getName());
        message.setGetter(useId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        message.setMesType(MessageType.MESSAGE_PRIVATE_INF);

        //用Object流发送message
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.get(u.getName()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方法：发送群发信息
    public void allMessage(String content){
        //编写发送信息message
        Message message = new Message();
        message.setSender(u.getName());
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_ALL_MES);
        message.setSendTime(new Date().toString());

        //发送群发信息
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.get(u.getName()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //方法：指定接收者发送文件
    public void SendFile(String getter , String src , String dest){
        //编辑message信息
        Message message = new Message();
        message.setSender(u.getName());
        message.setGetter(getter);
        message.setSrc(src);
        message.setDest(dest);
        message.setSendTime(new Date().toString());
        message.setMesType(MessageType.MESSAGE_PRIVATE_FILE);



        //先把本机位置fileAddress的内容存放到字节数组中,
        // 再把字节数组buf放到message.setBuf();
        byte[] buf = new byte[(int)new File(src).length()];//创建字节数组的大小是文件大小
        try {
            FileInputStream inputStream = new FileInputStream(src);
            inputStream.read(buf);//把文件转化成字节，存到buf字节数组中
            message.setBuf(buf);//并存进meaasge中
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        //发送message,用序列化Object，把message实例对象传送到服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.get(u.getName()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
