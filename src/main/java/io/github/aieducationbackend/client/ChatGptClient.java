package io.github.aieducationbackend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiRequestDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiResponseDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class ChatGptClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Value("${spring.ai.openai.chat.base-url}")
    private String url;

    @Value("${spring.ai.openai.chat.api-key}")
    private String apiKey;

    public ChatGptApiResponseDTO sendPrompt(ChatGptApiRequestDTO chatGptApiRequestDto) {

        String body;
        try {
            body = new ObjectMapper().writeValueAsString(chatGptApiRequestDto);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się połączyć z interfejsem zewnętrznym.");
        }

        RequestBody requestBody = RequestBody.create(body, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .callTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Nie udało się połączyć z interfejsem zewnętrznym.");
            }

            return new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), ChatGptApiResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się połączyć z interfejsem zewnętrznym.");
        }
    }

}
