package comment;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/12 19:46
 */
public interface MessageType {
    //在接口中定义一些常量
    //不同常量的之表示不同的信息类型
    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMMENT_MES = "3";//普通信息
    String MESSAGE_GET_ONLINE_FRIEND = "4";//请求服务器返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5";//返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_PRIVATE_INF = "7";//私聊信息
    String MESSAGE_ALL_MES = "8";//群发信息
    String MESSAGE_PRIVATE_FILE = "9";//私发文件
    String MESSAGE_TO_ALL = "10";//服务器推发信息


}
