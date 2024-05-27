package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SocketManager {
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private ObjectMapper jsMapper;

    public Queue<ObjectNode> acceptedData;
    public Queue<ObjectNode> sendingData;
    public Property<ClientCommands> clientCommand;
    public Property<SocketActions> socketAction;
    public boolean jsonSent = false;

    public enum ClientCommands {
        sendJson,
        zero
    }



    public enum SocketActions {
        acceptJson,
        zero
    }

    public SocketManager() {
        try {
            acceptedData = new LinkedList<>();
            sendingData = new LinkedList<>();
            jsMapper = new ObjectMapper();
            clientCommand = new SimpleObjectProperty<>();
            socketAction = new SimpleObjectProperty<>();
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
    public void sendJson() {
        ObjectNode json = sendingData.remove();
        try  {
            socket.close();
            connect("147.45.247.99", 8080);
            out.writeObject(json);
            out.flush();
            jsonSent = true;
            acceptJson();
        } catch
        (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void acceptJson() {
        try {
            if (jsonSent) {
                ObjectNode inData = (ObjectNode) in.readObject();
                if (inData.isEmpty()) return;
                jsonSent = false;
                acceptedData.add(inData);
                Platform.runLater(() -> socketAction.setValue(SocketActions.acceptJson));
            }
        } catch(IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
}