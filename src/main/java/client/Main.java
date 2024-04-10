package client;

public class Main {
    public static void main() {
        Client client = new Client();
        client.regModel.username = "Igor";
        client.regModel.password = "123123123";
        client.registration();
    }
}
