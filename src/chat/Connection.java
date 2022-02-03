package chat;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = (ObjectOutputStream) socket.getOutputStream();
        this.in = (ObjectInputStream) socket.getInputStream();
    }

    public synchronized void send(Message message) throws IOException {
        out.writeObject(message);
    }

    public synchronized Message receive() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }
}


