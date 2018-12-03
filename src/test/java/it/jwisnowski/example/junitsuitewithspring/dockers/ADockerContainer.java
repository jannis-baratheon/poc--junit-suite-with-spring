package it.jwisnowski.example.junitsuitewithspring.dockers;

import java.util.UUID;

public class ADockerContainer {
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

    public String getHost() {
        return "localhost";
    }

    public int getPort() {
        return 123;
    }
}
