package qqclient.view;

import qqclient.service.UserClientService;

import java.util.Scanner;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/12 20:07
 * QQ用户的菜单界面
 */
public class QQView {
    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("退出客户端");
    }
    @SuppressWarnings("all")
    private UserClientService ucs = new UserClientService();
    private boolean loop = true;
    private String key = "";
    private void mainMenu(){
        while(loop){
            System.out.println("=============欢迎登录=============");
            System.out.println("\t\t1、登录系统");
            System.out.println("\t\t2、退出系统");
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入您的选择：");
            key = scanner.next();
            switch (key){
                case "1" :
                        System.out.println("请输入用户号：");
                        String userId = scanner.next();
                        System.out.println("请输入密  码：");
                        String pwd = scanner.next();
                        //拿到账户密码后，发送一个user对象，去验证
                        //验证正确则进入下一级菜单
                        if (ucs.Check(userId,pwd)) {
                            System.out.println("=============欢迎(用户 " + userId +"  登陆成功)=============");
                            while (loop) {
                                System.out.println("\n=============用户菜单("+ userId +")=============");
                                System.out.println("\t\t1、显示在线用户列表");
                                System.out.println("\t\t2、群发消息");
                                System.out.println("\t\t3、私聊消息");
                                System.out.println("\t\t4、发送文件");
                                System.out.println("\t\t9、退出系统");

                                System.out.println("请输入您的选择：");
                                key = scanner.next();
                                switch (key) {
                                    case "1"://在线用户列表
                                        //1、编写方法，用于向服务器发送请求返回在线用户列表
                                        ucs.OnlineFriendList();
                                        break;
                                    case "2":
                                        //群发信息
                                        //调用方法：发送信息给所有人（在线）
                                        Scanner scanner2 = new Scanner(System.in);
                                        System.out.println("请输入发送的信息：");
                                        String content2  = scanner2.next();
                                        ucs.allMessage(content2);
                                        break;
                                    case "3":
                                        //私发信息
                                        //调用方法：发送message信息给接受者
                                        Scanner scanner1 = new Scanner(System.in);
                                        System.out.println("请输入用户名：");
                                        String UserId = scanner1.next();
                                        System.out.println("请输入发送的信息：");
                                        String content  = scanner1.next();
                                        ucs.PrivateChat(UserId,content);
                                        break;
                                    case "4":
                                        //发送文件
                                        //调用方法：选择要本机调用的文件，接收者，接收者存放位置
                                        Scanner scanner3 = new Scanner(System.in);
                                        System.out.println("请输入用户名：");
                                        String getter = scanner3.next();//接收者
                                        System.out.println("请输入本机文件地址：");
                                        String src = scanner3.next();//本机调用的文件
                                        System.out.println("请输入存放文件地址：");
                                        String dest = scanner3.next();//接收者存放位置
                                        ucs.SendFile(getter,src,dest);
                                        break;
                                    case "9":
                                        //调用个方法，使得程序正常退出
                                        ucs.logout();
                                        loop = false;
                                        break;
                                }
                            }
                        } else {
                            System.out.println("验证失败");
                        }
                    break;
                case "2" :
                    System.out.println("退出成功！");
                    loop = false;
                    break;
            }
        }
    }

}
