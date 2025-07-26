package net.engineeringdigest.journalApp.repository;

import net.engineeringdigest.journalApp.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    //SpringData MongoDB ke dwara ek inteface provide kiya gaya hai jise bolte hai MongoRepository
    User findByUsername (String username);

    void deleteByUsername(String username);
}
