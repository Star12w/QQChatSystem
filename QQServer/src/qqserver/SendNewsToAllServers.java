package qqserver;

import comment.Message;
import comment.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/15 23:13
 * 服务器向所有在线用户推发新闻信息
 */
public class SendNewsToAllServers implements Runnable{
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (true) {
            System.out.println("请输入需要服务器推送的信息[exit表示取消推送服务]：");
            String content = scanner.next();
            //如果不想要，推送服务，exit 退出SendNewsToAllServers线程即可
            if("exit".equals(content)){
                System.out.println("服务器推送服务取消！");
                break;
            }

            //编写message
            Message message = new Message();
            message.setContent(content);
            message.setMesType(MessageType.MESSAGE_TO_ALL);
            message.setSendTime(new Date().toString());

            //发给所有在线用户
            //拿到线程集合
            ConcurrentHashMap<String, ServerThread> hs = ManageServerThread.getHs();
            for(String s : hs.keySet()){//遍历
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(hs.get(s).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
