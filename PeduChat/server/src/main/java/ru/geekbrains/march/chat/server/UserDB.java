package ru.geekbrains.march.chat.server;

public class UserDB {
    private String login;
    private String password;
    private String nick;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public UserDB(String login, String password, String nick) {
        this.login = login;
        this.password = password;
        this.nick = nick;
    }
}
