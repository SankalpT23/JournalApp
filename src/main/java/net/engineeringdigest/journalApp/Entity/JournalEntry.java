package net.engineeringdigest.journalApp.Entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.engineeringdigest.journalApp.ENUMS.Sentiment;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

//POJO(Plain Old Java Object )
@Document(collection = "journal_entries") //As a Row(JournalEntry)
@Getter
@Setter
public class JournalEntry {
    @Id
    private ObjectId id;
    @NonNull
    private String title;
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;
}
