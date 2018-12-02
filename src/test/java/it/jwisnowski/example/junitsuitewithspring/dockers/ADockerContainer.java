package it.jwisnowski.example.junitsuitewithspring.dockers;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public class ADockerContainer implements Closeable {
    private UUID data;

    public void start() {
        System.out.println("Start container");
        data = UUID.randomUUID();
    }

    public void stop() {
        System.out.println("Stop container");
    }

    public String getData() {
        return data.toString();
    }

    @Override
    public void close() throws IOException {
        stop();
    }
}
