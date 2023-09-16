package qqserver;

import comment.Message;
import comment.MessageType;
import comment.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/14 10:33
 * 服务器端
 */
public class qqServer {
    private User user;
    public static void main(String[] args) {
        new qqServer();
    }
    private ServerSocket ss;
    private static ConcurrentHashMap<String,User> hs = new ConcurrentHashMap<>();

    static {
        hs.put("111",new User("111","123"));
        hs.put("222",new User("222","123"));
        hs.put("333",new User("333","123"));
        hs.put("444",new User("444","123"));
        hs.put("555",new User("555","123"));
    }
    //写一个判断用户是否正确的方法
    public boolean check(String UserId,String pwd) {
        User user = hs.get(UserId);
        if(user == null){ return false; }  //说明UserId没有在key中
        if(!(user.getPasswd().equals(pwd))){ return false; } //用户名正确，密码不正确
        return true;
    }
    public qqServer() {
        System.out.println("服务器正在等待连接中....");
        try {
            //由于 SendNewsToAllServers 是实现了Runnable接口 需要Thread对象开启线程
            Thread thread = new Thread(new SendNewsToAllServers());
            thread.start();//开启服务器推送服务线程

            ss = new ServerSocket(9999);
            //使用while(true)是保证监听端口可以持续监听，而不是监听一次就结束
            while(true) {
                Socket socket = ss.accept();//得到数据
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                user = (User) ois.readObject();//强转User类型
                //由于成败与否都需要给客户端发信息，所以在这里创建message对象
                Message message = new Message();
                if(check(user.getName(),user.getPasswd())){//用户验证成功
                    //给客户端发一个message成功信息
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //把message发送回客户端
                    oos.writeObject(message);

                    //发送后就需要构建持续稳定的客户端-服务器数据通道
                    //线程
                    ServerThread st = new ServerThread(socket,user.getName());
                    st.start();//启动线程
                    //把线程加入到线程集合中
                    ManageServerThread.addServerThread(user.getName(),st);
                } else {
                    //验证失败
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);//给客户端发一个message失败信息
                    oos.writeObject(message);//把message发送回客户端
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //如果运行到finally，意味着while循环停止，服务器监听系统不在监听，只需要关闭ServerSocket即可
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
