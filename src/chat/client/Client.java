package chat.client;

import chat.Connection;
import chat.ConsoleHelper;
import chat.Message;
import chat.MessageType;

import java.io.IOException;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Пользователь! Введи адрес сервера к которому будем подключаться: ");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Пользователь! Введи ПОРТ сервера к которому будем подключаться: ");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Пользователь! Введи своё имя, данное тебе при рождении: ");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    public static void main(String[] args) {
        new Client().run();
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Ошибка!" + e.getMessage());
                return;
            }
        }
        ConsoleHelper.writeMessage(clientConnected ? "Соединение установлено. Для выхода наберите команду ‘exit’." :
                                                     "Произошла ошибка во время работы клиента.");
        while (true) {
            ConsoleHelper.writeMessage("Введите сообщение: ");
            String msg = ConsoleHelper.readString();
            if (msg.equalsIgnoreCase("exit")) {
                break;
            }
            if (shouldSendTextFromConsole()) {
                sendTextMessage(msg);
            }
        }
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Что-то пошло не так! Сообщение не отправлено");
            clientConnected = false;
        }
    }

    public class SocketThread extends Thread {

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("К нам присоединился " + userName + ", прошу любить и жаловать!");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("К сожалению, чатланин " + userName + ", покинул нас.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            Client.this.notify();
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                switch (message.getType()) {
                    case NAME_REQUEST:
                        connection.send(new Message(MessageType.USER_NAME, getUserName()));
                        break;
                    case NAME_ACCEPTED:
                        notifyConnectionStatusChanged(true);
                        return;
                    case USER_NAME:
                    case TEXT:
                    case USER_REMOVED:
                    case USER_ADDED:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                switch (message.getType()) {
                    case TEXT:
                        processIncomingMessage(message.getData());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(message.getData());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(message.getData());
                        break;
                    case USER_NAME:
                    case NAME_ACCEPTED:
                    case NAME_REQUEST:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }
    }
}


