package frameforge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketManager {
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private ObjectMapper jsMapper;

    ExecutorService pool;
    Queue<ObjectNode> acceptedData;
    Queue<ObjectNode> sendingData;
    Property<ClientCommands> clientCommand;
    Property<SocketActions> socketAction;

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
            pool = Executors.newFixedThreadPool(2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Listeners:
    public void sendJson() {
        ObjectNode json = sendingData.remove();
        try  {
            socket.close();
            connect("188.225.82.247", 8080);
            out.println(jsMapper.writeValueAsString(json));
            pool.execute(this::acceptJson);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void acceptJson() {
        try {
            String inData = in.readLine();
            if (inData == null || inData.isEmpty()) return;
            System.out.println(inData);
            ObjectNode json = (ObjectNode) jsMapper.readTree(inData);
            acceptedData.add(json);
            Platform.runLater(() -> socketAction.setValue(SocketActions.acceptJson));
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}