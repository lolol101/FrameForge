package ru.server;

import java.net.Socket;

public class ClientHandler {
    public final Socket socket;
    private int userId;

    public ClientHandler(Socket socket, int userId) {
        this.socket = socket;
        this.userId = userId;
    }


}
