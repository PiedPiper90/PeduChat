package ru.geekbrains.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                while (true) { // Цикл авторизации
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")) {
                        // login Bob
                        String login = msg.split("\\s")[1];
                        String password = msg.split("\\s")[2];

//                        if (server.isUserOnline(usernameFromLogin)) {
//                            sendMessage("/login_failed Current nickname is already used");
//                            continue;
//                        }else if(server.wrongPassword(passwordFromLogin))
//
//                        username = usernameFromLogin;
//                        sendMessage("/login_ok " + username);
//                        server.subscribe(this);
//                        break;
                        UserDB user = server.userExist(login, password);
                        if (user == null) {
                            sendMessage("/login_failed wrong login/password");
                            continue;
                        } else {
                            username=user.getNick();
                            sendMessage("/login_ok " + username);
                            server.subscribe(this);
                            break;

                        }
                    }
                }


                while (true) { // Цикл общения с клиентом
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        executeCommand(msg);
                        continue;
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    private void executeCommand(String cmd) {
        // /w Bob Hello, Bob!!!
        if (cmd.startsWith("/w ")) {
            String[] tokens = cmd.split("\\s", 3);
            server.sendPrivateMessage(this, tokens[1], tokens[2]);
            return;
        } else if (cmd.startsWith("/change_nick ")) {
            String[] tokens = cmd.split("\\s", 2);
            if (!server.isUserOnline(tokens[1])) {
                username = tokens[1];
                server.broadcastClientsList();
            } else {
                sendMessage("Имя занято");
            }
        }
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
