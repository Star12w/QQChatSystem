package qqserver;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/14 11:28
 * 目的：用于管理创建包含Socket的线程
 */
public class ManageServerThread {
    private static ConcurrentHashMap<String, ServerThread> hs = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ServerThread> getHs() {
        return hs;
    }

    public static void addServerThread(String UserId, ServerThread serverThread){
        hs.put(UserId,serverThread);
    }
    public static ServerThread get(String UserId){
        return hs.get(UserId);
    }
    //用于返回一个String 类型的在线客户列表
    public static String RETOnlineList(){
        String online = "";
        //遍历集合中的key
        for(String s : hs.keySet()){
            online += s + " ";
        }
        return online;
    }

    //删除线程
    public static void removeThread(String UserId){
        hs.remove(UserId);
    }
}
