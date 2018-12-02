package it.jwisnowski.example.junitsuitewithspring;

import it.jwisnowski.example.junitsuitewithspring.dockers.ADockerContainer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestClass1 extends AbstractTestClass {

    @Autowired
    private ADockerContainer aDockerContainer;

    @Test
    public void test() {
        System.out.println("TestClass1 test " + aDockerContainer.getData());
    }
}
