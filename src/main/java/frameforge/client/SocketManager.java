package frameforge.client;

import javafx.beans.property.SimpleObjectProperty;
import java.util.concurrent.ExecutorService;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import javafx.beans.property.Property;
import javafx.application.Platform;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

public class SocketManager {
    private Socket socket = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String ip;
    private int port;

    public ExecutorService pool;
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
            this.ip = ip;
            this.port = port;
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
            connect(ip, port);
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
                Object inData = in.readObject();
                socket.close();
                dataSent = false;
                if (inData == null) return;
                acceptedData.add(inData);
                Platform.runLater(() -> socketAction.setValue(SocketActions.acceptData));
            }
        } catch(IOException | ClassNotFoundException | NoSuchElementException e){
            System.out.println(e.getMessage());
        }
    }
}