package controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
public class MessagesController {

    private final WebClient webClient;

    public MessagesController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> messages() {
        return this.webClient.get()
                .uri("http://localhost:8090/messages")
                .retrieve()
                .toEntityList(Message.class)
                .block();
    }

    public record Message(String message) {
    }

}
