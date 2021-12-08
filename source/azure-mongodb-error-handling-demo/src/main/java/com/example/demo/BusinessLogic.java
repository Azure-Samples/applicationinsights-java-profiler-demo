package com.example.demo;

import java.util.Arrays;
import java.util.List;

import com.example.demo.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BusinessLogic {
    private static final Logger logger = LoggerFactory.getLogger(BusinessLogic.class);
    //@Autowired
    private static final String webServiceMainUrl = "http://localhost:8080";

    /**
     * Run all business logic in a non-blocking fashion using reactive. See:
     * https://spring.io/reactive And: https://projectreactor.io/
     */
    public void run() {
        logger.info("Running: BusinessLogic.run()");
        User user1 = new User("josuemb", "Josué", "Martínez Buenrrostro");
        User user2 = new User("pnair", "Prasad", "Nair");
        User user3 = new User("etseitlin", "Eugene", "Tseitlin");
        List<User> users = Arrays.asList(new User("darrich", "Darren", "Rich"),
                new User("chandreshaw", "Chandresh", "Awasthi"));
        //Executors.newSingleThreadExecutor().submit(UserController.memorySink);
        try {
            Flux<Object> allTasks;
            allTasks = Flux.concat(deleteAllUsers(), createUser(user1), createUser(user2),
                    createUser(user3), createUsers(users), getAllUsers(), getUser("josuemb"));
            allTasks.subscribe(
                    // Consumer
                    i -> System.out.println(i),
                    // Error consumer
                    error -> System.out.println("Do something with error"));
        } catch (BusinessException businessException) {
            logger.error("It was a business exception", businessException);
        }
        // AVOID catch(Exception e)
    }

    private static Mono<Object> deleteAllUsers() {
        logger.info("Starting: BusinessLogic.deleteAllUsers()");
        WebClient client = WebClient.create(webServiceMainUrl);
        return client.delete().uri("/user/deleteAll").exchange()
                .flatMap(clientResponse -> clientResponse.statusCode().is2xxSuccessful()
                        ? Mono.empty()
                        : clientResponse.createException());
    }

    private static Mono<User> createUser(User user) {
        logger.info("Running: BusinessLogic.createUser()");
        WebClient client = WebClient.create(webServiceMainUrl);
        return client.post().uri("/user").accept(MediaType.APPLICATION_JSON).bodyValue(user)
                .retrieve()
                // handle status
                .onStatus(HttpStatus::isError, clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        logger.error("ATTENTION: ERROR ON SERVER HOSTING WS {}",
                                clientResponse.statusCode());
                    }
                    return clientResponse.createException();
                }).bodyToMono(User.class);
    }

    private static Flux<User> createUsers(List<User> users) {
        logger.info("Running: BusinessLogic.createUsers()");
        WebClient client = WebClient.create(webServiceMainUrl);
        return client.post().uri("/user/insertUsers").accept(MediaType.APPLICATION_JSON)
                .bodyValue(users).retrieve()
                // handle status
                .onStatus(HttpStatus::isError, clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        logger.error("AATENTION: ERROR ON SERVER HOSTING WS {}",
                                clientResponse.statusCode());
                    }
                    return clientResponse.createException();
                }).bodyToFlux(User.class);
    }

    private static Flux<User> getAllUsers() {
        logger.info("Running: BusinessLogic.getAllUsers()");
        WebClient client = WebClient.create(webServiceMainUrl);
        Flux<User> users = client.post().uri("/user/getAllUsers").retrieve()
                // handle status
                .onStatus(HttpStatus::isError, clientResponse -> {
                    return clientResponse.createException();
                }).bodyToFlux(User.class);
        users.toStream().forEach(System.out::println);
        return users;

    }

    private static Mono<User> getUser(String id) throws BusinessException {
        logger.info("Running: BusinessLogic.getUser(String id)");
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("ERROR: id for user cannot be neither null nor blank");
        }
        WebClient client = WebClient.create(webServiceMainUrl);
        String dynamicUri = String.format("/user/getUser/%s", id);
        return client.get().uri(dynamicUri).retrieve()
                // handle status
                .onStatus(HttpStatus::isError, clientResponse -> {
                    return clientResponse.createException();
                }).bodyToMono(User.class);

    }
}
