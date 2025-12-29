package net.engineeringdigest.journalApp.Services;

import net.engineeringdigest.journalApp.ENUMS.Sentiment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SentimentAnalysisService {

    public Sentiment getSentiment(String text) {
        if (text == null || text.isEmpty()) return Sentiment.ANXIOUS; // Default

        text = text.toLowerCase();

        // Basic Keyword lists
        List<String> happyWords = Arrays.asList("happy", "excited", "love", "joy", "great", "awesome", "good");
        List<String> sadWords = Arrays.asList("sad", "crying", "depressed", "unhappy", "bad", "lonely");
        List<String> angryWords = Arrays.asList("angry", "mad", "furious", "hate", "rage", "annoyed");
        List<String> anxiousWords = Arrays.asList("anxious", "nervous", "scared", "worried", "fear");

        int happyCount = countMatches(text, happyWords);
        int sadCount = countMatches(text, sadWords);
        int angryCount = countMatches(text, angryWords);
        int anxiousCount = countMatches(text, anxiousWords);

        // Determine the strongest sentiment
        int max = Math.max(happyCount, Math.max(sadCount, Math.max(angryCount, anxiousCount)));

        if (max == 0) return Sentiment.HAPPY; // Default neutral/positive
        if (max == happyCount) return Sentiment.HAPPY;
        if (max == sadCount) return Sentiment.SAD;
        if (max == angryCount) return Sentiment.ANGRY;
        return Sentiment.ANXIOUS;
    }

    private int countMatches(String text, List<String> words) {
        int count = 0;
        for (String word : words) {
            if (text.contains(word)) count++;
        }
        return count;
    }
}