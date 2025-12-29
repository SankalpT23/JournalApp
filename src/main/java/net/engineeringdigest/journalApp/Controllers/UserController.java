package net.engineeringdigest.journalApp.Controllers;

import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.API.Response.WeatherResponse;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Services.UserService;
import net.engineeringdigest.journalApp.Services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user") //Adds Mapping to the whole class
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;


    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDb = userService.findByUsername(username);
        // Only update username if provided and different
        if (!user.getUsername().isEmpty() && !user.getUsername().equals(userInDb.getUsername())) {
            userInDb.setUsername(user.getUsername());
        }
        if (!user.getPassword().isEmpty()) {
            userInDb.setPassword(user.getPassword());
        }
        userService.saveNewUser(userInDb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> greetings(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse =  weatherService.getWeather("Mumbai");
        String greeting = "";
        if (weatherResponse != null){
            greeting = " Weather Feels Like " + weatherResponse.getCurrent().getTempInC();
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + greeting,HttpStatus.OK);
    }
}
