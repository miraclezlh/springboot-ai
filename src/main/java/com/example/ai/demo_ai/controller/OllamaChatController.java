package com.example.ai.demo_ai.controller;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @author 70635
 */
@RestController
@CrossOrigin("*")
public class OllamaChatController {
    private final ClassPathResource imageResource = new ClassPathResource("/multimodal.test.png");

    private final ChatClient chatClient;
    private final OllamaChatModel chatModel;

    OllamaChatController(ChatClient.Builder chatClientBuilder, OllamaChatModel chatModel) {
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
    }

    @PostMapping("/")
    public String home(OllamaApi.Model model) {
        return "index";
    }

    @PostMapping("/chat")
    public String chat(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/fluxChat")
    public Flux<String> fluxChat(String question) {
        return this.chatModel.stream(chatClient.prompt()
                .user(question)
                .call()
                .content());
    }

    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.chatModel.call(message));
    }

    @GetMapping("/imageChat")
    public String imageChat() {
        var userMessage = UserMessage.builder()
                .text("Explain what do you see in this picture?")
                .media(List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageResource)))
                .build();

        var response = this.chatModel.call(new Prompt(List.of(userMessage)));

        return response.toString();
    }
}
