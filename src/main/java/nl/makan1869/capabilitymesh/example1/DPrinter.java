package nl.makan1869.capabilitymesh.example1;

import org.springframework.stereotype.Component;

@Component
public class DPrinter implements IPrinter {

    @Override
    public void print() {
        System.out.println("D");
    }
}
