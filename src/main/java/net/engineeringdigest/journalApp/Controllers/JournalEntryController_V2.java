package net.engineeringdigest.journalApp.Controllers;

import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.Services.SentimentAnalysisService;
import net.engineeringdigest.journalApp.Services.UserService;
import net.engineeringdigest.journalApp.Services.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController //It is used to tell that this class is made for Http requests
@RequestMapping("/journal") //Adds Mapping to the whole class
public class JournalEntryController_V2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    //@GetMapping
    //It should Always be Public So that it can be accessed or invoked
//    public List<JournalEntry> getAll(){
//        return journalEntryService.getAll();
//    }

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username); // Telling me that User = Sankalp/Rahul/Amit etc.
        List<JournalEntry> all = user.getJournalEntries(); //Find the Details Of the user in journal entries
        if (all!=null && !all.isEmpty()){ //If there is Entry in the list then return that entry
            return new ResponseEntity<>(all,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Otherwise, 404 Not Found error
    }

//    @PostMapping
//    public JournalEntry createEntry(@RequestBody JournalEntry myEntry){
//        myEntry.setDate(LocalDateTime.now());
//        journalEntryService.saveUser(myEntry);
//        return myEntry;
//    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        // RequestBody -> Body se JSON me likha hua code lega aur PathVariable url se username legga
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            // CALL SENTIMENT ANALYSIS HERE
            String text = myEntry.getTitle() + " " + myEntry.getContent();
            myEntry.setSentiment(sentimentAnalysisService.getSentiment(text));
            journalEntryService.saveEntry(myEntry,username); // tell the service to save That Entry
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);// Successful --> 201 Created
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//Not successful --> Bad Request Given
        }
    }

//    @GetMapping("id/{myId}")
//    public JournalEntry getJournalEntryById(@PathVariable ObjectId myId){
//        return journalEntryService.findById(myId).orElse(null);
//        //Due To Optional
//    }

//    @GetMapping("id/{myId}")
//    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        User user = userService.findByUsername(username);
//        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
//        if (!collect.isEmpty()){
//            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId); // Find Entry in Service & save to journalEntry
//            if (journalEntry.isPresent()){ // If Entry Is Present Then
//                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND); //If not then again --> 404 Not Found
//    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        // 🔍 Pehle user ki list me try karo
        Optional<JournalEntry> fromUserList = user.getJournalEntries().stream()
                .filter(x -> x.getId().equals(myId))
                .findFirst();
        if (fromUserList.isPresent()) {
            return new ResponseEntity<>(fromUserList.get(), HttpStatus.OK);
        }
        // 🔍 Agar user list me nahi mila, to DB me try karo
        Optional<JournalEntry> fromDb = journalEntryService.findById(myId);
        if (fromDb.isPresent()) {
            return new ResponseEntity<>(fromDb.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = journalEntryService.deleteById(myId, username);//Told the Service to Delete It
        if (removed){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Success then 204 No Content
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Success then 204 No Content
        }
    }

    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if (!collect.isEmpty()){
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId); // Find Entry in Service & save to journalEntry
            if (journalEntry.isPresent()){ // If Entry Is Present Then
                JournalEntry old = journalEntry.get();
                old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
                // Title null toh nai hai agr hai toh update mat karo , Title empty string toh nai hai dont update agr blank hai toh , Agar dono true hai toh lelo nai toh purana rehne do , old.setTitle ke sahare oldobject ke andr finaltitle set ho raha hai
                old.setContent(newEntry.getContent()!=null && !newEntry.equals("") ? newEntry.getContent() : old.getContent());
                //Agar newEntry ka Content null nahi hai aur empty string bhi nahi hai, toh naya content set karo. Nahi toh purana hi rehne do.

                String text = old.getTitle() + " " + old.getContent();
                old.setSentiment(sentimentAnalysisService.getSentiment(text));

                journalEntryService.saveEntry(old);//After Updation Save It Again in Service
                return new ResponseEntity<>(old, HttpStatus.OK);//Return Updated Entry
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);//If Not --> 404 Not Found
    }
}
