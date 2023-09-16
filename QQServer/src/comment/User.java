package comment;

import java.io.Serializable;

/**
 * @author M_Dolin
 * @version 1.0
 * @date 2023/9/12 17:46
 * 用户
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String passwd;

    public User() {
    }

    public User(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
