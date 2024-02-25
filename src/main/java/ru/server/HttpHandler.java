package ru.server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HttpHandler implements Runnable {
    private static Socket socket;
    private static ObjectMapper jsMapper = new ObjectMapper();
    private enum ACTIONS {
        REGISTRATION,
        AUTHORAZATION,
        SET,
        GET, 
        UPDATE
    };

    public HttpHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handleRequest();
        } catch (Exception e) {
            System.out.println("Error (HttpHandler/run): " + e.getMessage());
            try {
                this.socket.close();
            } catch (IOException ae) {
                System.out.println("Error while close socket conn!");
            } finally {
                System.exit(0);
            }
        }
    }

    private void handleRequest() throws Exception {
        JsonNode req = getRequest();
        ACTIONS type = ACTIONS.valueOf(req.get("type").textValue());
        if (type == ACTIONS.REGISTRATION) {
            String name = req.get("username").textValue();
            System.out.println(name);
        }
        System.out.println(type);
    }

    // private static String getString() throws IOException {
    //     InputStream in = socket.getInputStream();
    //     DataInputStream dataIn = new DataInputStream(in);
    //     String req = dataIn.readUTF();
    //     dataIn.close();
    //     return req;
    // }

    // private static void sendString(Socket conn, String req) throws IOException {
    //     OutputStream out = conn.getOutputStream();
    //     DataOutputStream dataOut = new DataOutputStream(out);
    //     dataOut.writeUTF(req);
    //     dataOut.flush();
    //     dataOut.close();
    // }

    private static JsonNode getRequest() throws IOException {
        InputStream inStream = socket.getInputStream();
        String json = new BufferedReader(
            new InputStreamReader(inStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
        JsonNode jsNode = jsMapper.readTree(json);
        return jsNode;
    }
}
