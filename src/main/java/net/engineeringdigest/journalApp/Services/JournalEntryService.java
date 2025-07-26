package net.engineeringdigest.journalApp.Services;

import net.engineeringdigest.journalApp.Entity.JournalEntry;
import net.engineeringdigest.journalApp.Entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo; //Dependency Injection

    @Autowired
    private UserService userService;


    @Transactional //If One DataBase Fails to work then we ensure that the other Database Does Not Work at all
    public void saveEntry(JournalEntry journalEntry, String username){
        try {
            User user = userService.findByUsername(username); // With username, we get User
            journalEntry.setDate(LocalDateTime.now()); //Set Date
            JournalEntry saved = journalEntryRepo.save(journalEntry);// save the Entry in DB
            user.getJournalEntries().add(saved);//Add the Entry of the user in the list
            userService.saveUser(user);//Save User
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("An Error Occurred while saving the entry");
        }
    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepo.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepo.findAll();//All Entries get present
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepo.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String username){
        boolean removed = false;
        try {
            User user = userService.findByUsername(username); //With Help of Id we find user
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id)); // we remove Entry of the Matching id from the journal entry list
            if (removed) {
                userService.saveUser(user);//Save User
                journalEntryRepo.deleteById(id);//Delete From Database Also
            }
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("An Error Occured while Deleting the entry.", e);
        }
        return removed;
    }

}
//Controller --> Service --> Repository(Interface)
//Request Controller pe aati hai.
//Controller Service ko bolta hai kaam kar.
//Service Repo ke through DB me kaam karta hai.
//Agar User involved hai toh uske list update hoti hai.
//Response client ko milta hai.