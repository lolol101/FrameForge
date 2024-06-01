package frameforge.client;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;
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