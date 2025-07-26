package net.engineeringdigest.journalApp.API.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResponse {
    private Current current;

    @Getter
    @Setter
    public class Current{
        @JsonProperty("temp_c")
        private double tempInC;
        @JsonProperty("humidity")
        private int humidity;
        @JsonProperty("windchill_c")
        private double windChillInC;
        @JsonProperty("heatindex_c")
        private double heatIndexInC;
        @JsonProperty("dewpoint_c")
        private double dewPointInC;
    }
}





