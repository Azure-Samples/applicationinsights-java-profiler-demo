package com.example.demo.webservices;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.demo.MemorySink;
import com.example.demo.data.ReactiveUserRepository;
import com.example.demo.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(path = "/user")
public class UserController {
    public static MemorySink memorySink=new MemorySink();
    public boolean verify(String input) {
        try {
            Pattern pattern1 = Pattern.compile("User \\[firstName=(.+), id=.+, lastName=.+\\]");
            Pattern pattern2 = Pattern.compile("User \\[firstName=.+, id=(.+), lastName=.+\\]");
            Pattern pattern3 = Pattern.compile("User \\[firstName=.+, id=.+, lastName=(.+)\\]");
            Matcher matcher1 = pattern1.matcher(input);
            Matcher matcher2 = pattern2.matcher(input);
            Matcher matcher3 = pattern3.matcher(input);
            if (matcher1.find() && matcher2.find() && matcher3.find())
                return ! (matcher1.group(1).isEmpty() || matcher2.group(1).isEmpty() || matcher3.group(1).isEmpty());
            return false;
        }
        catch (Exception ex) {
            return false;
        }
    }
    public boolean reverify(String input){
        return verify(input);
    }

    @Autowired
    private ReactiveUserRepository userRepository;

    @DeleteMapping("/deleteAll")
    Mono<Void> deleteAllUsers() {
        return userRepository.deleteAll();
    }

    @PostMapping
    Mono<User> insertUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/insertUsers")
    Flux<User> insertUsers(@RequestBody List<User> users) {
        return userRepository.saveAll(users);
    }

    @RequestMapping("/getAllUsers")
    Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @RequestMapping("/getAllUsersLeaky")
    Flux<User> getAllUsersLeaky() {
        Executors.newSingleThreadExecutor().submit(UserController.memorySink);
        return getAllUsers();
    }

    @RequestMapping("/getAllUsersThink")
    Flux<User> getAllUsersLeakyThink() {
        Flux<User> validatedUsers = userRepository.findAll().filter(user -> verify(user.toString()));
        Flux<String> hashed = validatedUsers.map(user -> genSecurePassword(user.getLastName(), user.getFirstName().getBytes()));
        hashed.subscribe(System.out::println);
        return userRepository.findAll();
    }

    @RequestMapping("/getUser/{id}")
    Mono<User> getUser(@PathVariable(name = "id") String id) {
        return userRepository.findById(id);
    }

    private String genSecurePassword(String password, byte[] salt) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            for (int n=0; n<100000; n++) bytes = md.digest(bytes);
            return java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return password;
        }
    }
}
