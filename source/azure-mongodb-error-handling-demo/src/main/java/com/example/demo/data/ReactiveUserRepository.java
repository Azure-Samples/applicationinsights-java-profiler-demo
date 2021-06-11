package com.example.demo.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveUserRepository extends ReactiveMongoRepository<User, String> {
    
}
