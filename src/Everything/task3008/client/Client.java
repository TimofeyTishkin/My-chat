package Everything.task3008.client;


import Everything.task3008.*;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;
    protected String getServerAddress() {
            ConsoleHelper.writeMessage("Write down server address you need");
            return ConsoleHelper.readString();
    }
    protected int getServerPort(){
            ConsoleHelper.writeMessage("Write down server port you need");
            return ConsoleHelper.readInt();
    }
    protected String getUserName(){
            ConsoleHelper.writeMessage("Write your nick");
            return ConsoleHelper.readString();
    }
    protected boolean shouldSendTextFromConsole(){
        return true;
    }
    protected SocketThread getSocketThread(){
        return new SocketThread();
    }
    protected void sendTextMessage(String text){
        try{
            Message message = new Message(MessageType.TEXT, text);
            connection.send(message);
        } catch (IOException e){
            this.clientConnected = false;
            ConsoleHelper.writeMessage("New Error: "+e);
        }
    }
    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Error:"+e);
                return;
            }
        }
        if(clientConnected){
            ConsoleHelper.writeMessage("Соединение установлено.Для выхода наберите команду 'exit'.");
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
        while (clientConnected){
            String clientString = ConsoleHelper.readString();
            if(clientString.equals("exit")) break;
            if(shouldSendTextFromConsole()){
                sendTextMessage(clientString);
            }
        }
    }

    public static void main(String[] args) {
        new Client().run();
    }
    public class SocketThread extends Thread{

        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }
        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage(userName+" joined chat");
        }
        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage(userName+" left chat");
        }
        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }
        protected void clientHandshake() throws IOException, ClassNotFoundException{
            while(true){
                Message message = Client.this.connection.receive();
                if(message.getType() == MessageType.NAME_REQUEST){
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else if(message.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }
        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            while(true){
                Message message = Client.this.connection.receive();
                if(message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if(message.getType() == MessageType.USER_ADDED){
                    informAboutAddingNewUser(message.getData());
                } else if(message.getType() == MessageType.USER_REMOVED){
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }
        public void run(){
            try {
                String address = getServerAddress();
                int port = getServerPort();
                Socket socket = new Socket(address, port);
                Connection connection = new Connection(socket);
                Client.this.connection = connection;
                clientHandshake();
                clientMainLoop();

            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }
    }
}
