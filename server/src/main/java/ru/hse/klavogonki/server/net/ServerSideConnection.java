package ru.hse.klavogonki.server.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSideConnection implements Closeable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ServerSideConnection() {
    }

    public ServerSideConnection(Socket s) throws IOException {
        setSocket(s);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket s) throws IOException {
        if (socket != null) {
            try {
                close();
            } catch (IOException e) {
                System.out.println("Can't close old socket: " + e);
            }
        }
        socket = s;
        outputStream = new ObjectOutputStream(s.getOutputStream());
        inputStream = new ObjectInputStream(s.getInputStream());
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public void close() throws IOException {
        socket.close();
    }
}
