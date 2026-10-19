package nl.makan1869.capabilitymesh.example1;

import org.springframework.stereotype.Component;

@Component
public class BPrinter implements IPrinter {

    @Override
    public void print() {
        System.out.println("B");
    }
}
