package nl.makan1869.capabilitymesh.example1;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class SmartPrinter implements ICharacterPrinter {

    private final ChatClient chatClient;

    public SmartPrinter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public void print(Character ch) {
        String letter = chatClient.prompt()
                .user("Reply with only the uppercase letter %s. No punctuation, no explanation, just the single character."
                        .formatted(ch))
                .call()
                .content();
        System.out.println(letter);
    }
}
