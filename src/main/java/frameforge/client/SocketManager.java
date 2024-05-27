package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import frameforge.serializable.JsonSerializable;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketManager {
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    ExecutorService pool;
    public Queue<Object> acceptedData;
    public Queue<Object> sendingData;
    public Property<ClientCommands> clientCommand;
    public Property<SocketActions> socketAction;
    public boolean dataSent = false;

    public enum ClientCommands {
        sendData,
        zero
    }

    public enum SocketActions {
        acceptData,
        zero
    }

    public SocketManager() {
        try {
            acceptedData = new LinkedList<>();
            sendingData = new LinkedList<>();
            clientCommand = new SimpleObjectProperty<>();
            socketAction = new SimpleObjectProperty<>();
            pool = Executors.newFixedThreadPool(2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Listeners:
    public void sendData() {
        Object data = sendingData.remove();
        try  {
            socket.close();
            connect("147.45.247.99", 8080);
            out.writeObject(data);
            out.flush();
            dataSent = true;
            pool.execute(this::acceptData);
        } catch
        (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void acceptData() {
        try {
            if (dataSent) {
                JsonSerializable inData = (JsonSerializable) in.readObject();
                if (inData == null) return;
                dataSent = false;
                acceptedData.add(inData);
                Platform.runLater(() -> socketAction.setValue(SocketActions.acceptData));
            }
        } catch(IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
}