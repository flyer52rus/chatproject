package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Введите порт сервера: ");
        ServerSocket serverSocket = null;
        try (ServerSocket ss = new ServerSocket(ConsoleHelper.readInt()))
        {
            ConsoleHelper.writeMessage("Сервер успешно запущен!");
            while (true) {
                new Handler(ss.accept()).start();
            }
        } catch (IOException ex) {
            ConsoleHelper.writeMessage("Произошла ошибка " + ex.getMessage());
        }
    }

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                entry.getValue().send(message);
            }
        } catch (IOException ex) {
            ConsoleHelper.writeMessage("Сообщение не отправлено. Произошла ошибка!");
        }
    }


    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST, "Введите имя пользователя:"));
                Message message = connection.receive();
                if (message.getType() == MessageType.USER_NAME) {
                    String nameUser = message.getData();
                    if (nameUser != null && nameUser.length() > 0 && !connectionMap.containsKey(nameUser)) {
                        connectionMap.put(nameUser, connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED, null));
                        return nameUser;
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if(!(entry.getKey().equalsIgnoreCase(userName))) {
                    connection.send(new Message(MessageType.USER_ADDED, userName));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                if (connection.receive().getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + connection.receive().getData()));
                } else {
                    ConsoleHelper.writeMessage("Ошибка! данное сообщение не является типом TEXT");
                }
            }
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установлено новое соединение с удаленным адресом " + socket.getRemoteSocketAddress());
            String nameUser = null;
            try (Connection connection = new Connection(socket))
                {
                nameUser = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, "К нам присоединился "
                                                                                     + nameUser
                                                                                        + " Поприветствуем его!"));
                sendListOfUsers(connection, nameUser);
                serverMainLoop(connection, nameUser);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом" + e.getMessage());
            } catch (ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом" + e.getMessage());
            } finally {
                if (nameUser != null) {
                    connectionMap.remove(nameUser);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED,  nameUser));
                    ConsoleHelper.writeMessage("Соединение с удаленным адресом "
                                                    + socket.getRemoteSocketAddress()
                                                        + " закрыто");
                }
            }
        }
    }
}

