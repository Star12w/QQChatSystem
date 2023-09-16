package qqserver;

import comment.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/15 23:46
 * 存放离线信息
 * 思路：在服务器再开一个线程，处理离线信息
 */
public class OfflineStorageMessage {
    //用一个 ConcurrentHashMap<String, ArrayList> ct  集合存放离线信息
    private ConcurrentHashMap<String, ArrayList<Message>> ct = new ConcurrentHashMap<>();

    //添加离线信息
    public void addMessage(String getter, Message message) {
        // 获取与接收者关联的离线消息列表
        ArrayList<Message> messageList = ct.get(getter);

        //如果messageList是空的，也就是说，原来没有getter的离线信息，需要加入
        if(messageList == null){
            messageList = new ArrayList<>();
            ct.put(getter,messageList);
        }

        //之前有getter，并将新的离线消息添加到列表
        messageList.add(message);
        System.out.println("添加成功");
    }

    //将在线用户遍历，并判断在线列表中的用户是否有离线信息
    //如果有就发送
    public void dealMessage(){
        ConcurrentHashMap<String, ServerThread> hs = ManageServerThread.getHs();
        //遍历在线用户列表列表
        for(String s : hs.keySet()) {
            //如果不为空，意味着当前用户有离线信息
            if (!(ct.get(s) == null)) {
                try {
                    //通过当前用户的socket的outputstream把离线信息传给当前用户
                    ObjectOutputStream oos = new ObjectOutputStream(
                            hs.get(s).getSocket().getOutputStream());
                    //遍历对应的ArrayList<Message>集合
                    for (Message message : ct.get(s)) {
                        //把message传送给getter
                        oos.writeObject(message);
                    }
                    //把离线信息传完之后，离线信息列表中删除对应的用户
                    ct.remove(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //通过线程，循环执行dealMessage()
    public void run() {
        while (true) {
            dealMessage();
        }
    }
}