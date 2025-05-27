package com.example.lineta_notification.client;

import com.example.lineta_notification.dto.response.UserDTO;
import com.example.lineta_notification.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("http://localhost:8080")
    private String userServiceUrl;

    public Map<String, UserDTO> getUsersByUsernames(Set<String> usernames) {
        String url = userServiceUrl + "/api/auth/users/batch-by-username";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<Set<String>> request = new HttpEntity<>(usernames, headers);

        ResponseEntity<UserDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                UserDTO[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.stream(response.getBody())
                    .collect(Collectors.toMap(UserDTO::getUsername, user -> user));
        }

        return new HashMap<>();
    }

    @Value("http://localhost:8081")
    private String userServiceUrl8081;

    public List<String> getFollowerUsernames(String followedId) {
        String url = userServiceUrl8081 + "/api/friend/users/followers-with-username/" + followedId;

        ResponseEntity<ApiResponse<List<String>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<String>>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (List<String>) response.getBody().getResult();
        }

        return Collections.emptyList();
    }



}
