package qqserver;

import com.sun.javafx.image.BytePixelSetter;
import comment.Message;
import comment.MessageType;
import comment.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/14 10:46
 * 此类目的： 登录成功后，构建线程，可同时处理多个用户
 */
public class ServerThread extends Thread{
    private Socket socket;
    private String UserId;

    public ServerThread(Socket socket, String userId) {
        this.socket = socket;
        this.UserId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    //持续接收来自客户端的信息
    @Override
    public void run() {
        while(true){
            try {
                System.out.println("服务器和客户端"+ UserId +" 保持通信，读取数据...");
                //与客户端连接
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //接收数据
                Message message = (Message) ois.readObject();

                //判断来自客户端信息类型
                //1、在线用户列表
                if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    System.out.println(message.getSender() + "要在线用户列表");
                    Message message1 = new Message();
                    String s = ManageServerThread.RETOnlineList();//调用方法，返回个String类型的在线用户列表
                    message1.setContent(s);//把列表赋给message中的Content变量
                    message1.setGetter(message.getSender());
                    message1.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);//把message的类型变成5，标记
                    //传给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message1);//传送

                }
                //2、无异常退出
                else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    //现在就在线程中接收信息
                    //1、从集合中删除message.getSender()对应的线程
                    ManageServerThread.removeThread(message.getSender());
                    //2、把对应的Socket也关闭
                    socket.close();
                    //3、最后把线程关闭,break,就是退出当前循环
                    System.out.println("用户 "+message.getSender() + "退出成功！");
                    break;
                }
                //3、私聊
                else if (message.getMesType().equals(MessageType.MESSAGE_PRIVATE_INF)) {
                    //通过服务器的通道，发给接收者getter
                    //遍历在线用户，判断是否在线
                    boolean foundOnlineUser = false;//判断是否找到用户，找到设置成true
                    ConcurrentHashMap<String, ServerThread> hs = ManageServerThread.getHs();
                    for(String s : hs.keySet()){
                        if(s.equals(message.getGetter())){
                            //通过message.getGetter()找到接收者的线程中的socket，并拿到OutputStream，并发送信息
                            ObjectOutputStream oos = new ObjectOutputStream(
                                    ManageServerThread.get(message.getGetter()).getSocket().getOutputStream());
                            oos.writeObject(message);
                            foundOnlineUser = true;// 找到在线用户
                            break;// 不再继续循环，因为消息已发送
                        }
                    }
                    //退出for循环意味着没有对应的在线用户
                    if(!foundOnlineUser) {
                        System.out.println("正在存储离线信息");
                        OfflineStorageMessage osm = new OfflineStorageMessage();
                        osm.addMessage(message.getGetter(),message);
                        osm.run();
                    }

                }
                //4、群发
                else if (message.getMesType().equals(MessageType.MESSAGE_ALL_MES)) {
                    //通过服务器通道，发送给所有在线人
                    //拿到所有人的线程一个一个发信息
                    //拿到线程集合(在线)
                    ConcurrentHashMap<String, ServerThread> hs = ManageServerThread.getHs();
                  for(String s : hs.keySet()){//遍历线程集合
                      if(!(message.getSender().equals(s))){
                          ObjectOutputStream oos = new ObjectOutputStream(hs.get(s).getSocket().getOutputStream());
                          oos.writeObject(message);
                      }
                  }
                }
                //5、私聊发送文件
                else if (message.getMesType().equals(MessageType.MESSAGE_PRIVATE_FILE)) {
                    //先通过线程集合找到接收者，在根据接收者的socket的outputstream发送meaasge（file）
                    //遍历在线用户列表，判断是否在线
                    boolean foundOnlineUser = false;//判断是否找到用户，找到设置成true
                    ConcurrentHashMap<String, ServerThread> hs = ManageServerThread.getHs();
                    for(String s : hs.keySet()){
                        //在 在线用户列表中有我们发送的接收者
                        if(s.equals(message.getGetter())){
                            ObjectOutputStream oos = new ObjectOutputStream(
                                    ManageServerThread.get(message.getGetter()).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                        //找到在线用户，退出for循环
                        foundOnlineUser = true;
                        break;
                    }
                    //退出for循环意味着没有对应的在线用户
                    if(!foundOnlineUser){
                        new OfflineStorageMessage().addMessage(message.getGetter(),message);
                    }
                } else {
                    System.out.println("其他类型暂不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
