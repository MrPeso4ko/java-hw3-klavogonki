package ru.hse.klavogonki.client.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSideConnection implements Closeable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public void setSocket(Socket s) throws IOException {
        if (socket != null) {
            try {
                close();
            } catch (IOException e) {
                System.out.println("Can't close old socket: " + e);
            }
        }
        socket = s;
        inputStream = new ObjectInputStream(s.getInputStream());
        outputStream = new ObjectOutputStream(s.getOutputStream());
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}
