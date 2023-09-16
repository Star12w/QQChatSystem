package qqclient.service;

import java.util.HashMap;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/13 17:51
 * 为了客户端的扩展，使用集合来管理线程（Socket）
 */
public class ManageClientConnectServerThread {
    private static HashMap<String,ClientConnectServerThread> hm = new HashMap<>();
    public static void addThread(String UserId,ClientConnectServerThread ccst){
        hm.put(UserId,ccst);
    }
    public static ClientConnectServerThread get(String UserId){
        return hm.get(UserId);
    }
}
