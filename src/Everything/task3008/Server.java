package Everything.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void sendBroadcastMessage(Message message){
        for(Map.Entry<String, Connection> pair : connectionMap.entrySet()){
            try {
                pair.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Error");
            }
        }
    }
    public static void main(String[] args) {
        int port = ConsoleHelper.readInt();
        try(ServerSocket serverSocket  = new ServerSocket(port)){
            System.out.println("сервер запущен");
            for(;;){
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static class Handler extends Thread{
        private Socket socket;
        public Handler(Socket socket){
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            while(true){
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if(answer.getType()!=MessageType.USER_NAME) continue;
                String userName = answer.getData();
                if(userName.isEmpty() || connectionMap.containsKey(userName)) continue;
                connectionMap.put(userName, connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }
        private void notifyUsers(Connection connection, String userName) throws IOException{
            for(Map.Entry<String, Connection> pair:connectionMap.entrySet()) {
                if (!userName.equals(pair.getKey())) {
                    connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                }
            }
        }
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{

            //Организовать бесконечный цикл
            while (true){
                // Принимать сообщение клиента
                Message message = connection.receive();

                // Если принятое сообщение – это текст
                if (message.getType() == MessageType.TEXT){
                    //  формировать новое текстовое сообщение
                    String messageText = userName + ": " + message.getData();
                    // Отправлять сформированное сообщение всем клиентам с помощью
                    sendBroadcastMessage(new Message(MessageType.TEXT, messageText));
                }
                else {
                    // Если принятое сообщение не является текстом, вывести сообщение об ошибке
                    ConsoleHelper.writeMessage("Error!");
                }
            }

        }
        public void run(){
            ConsoleHelper.writeMessage("было установлено соединение с удаленным адресом:"+socket.getRemoteSocketAddress());
            try(Connection connection = new Connection(this.socket)){
                String clientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, clientName));
                notifyUsers(connection, clientName);
                serverMainLoop(connection, clientName);
                connectionMap.remove(clientName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, clientName));
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("произошла ошибка при обмене данными с удаленным адресом");
            } finally {
                ConsoleHelper.writeMessage("соединение с удаленным адресом закрыто");
            }
        }
    }
}
