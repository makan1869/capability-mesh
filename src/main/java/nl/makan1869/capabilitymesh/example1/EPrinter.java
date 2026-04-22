package nl.makan1869.capabilitymesh.example1;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class EPrinter implements IPrinter {

    private final ChatClient chatClient;

    public EPrinter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public void print() {
        String letter = chatClient.prompt()
                .user("Reply with only the uppercase letter E. No punctuation, no explanation, just the single character.")
                .call()
                .content();
        System.out.println(letter);
    }
}
