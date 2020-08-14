import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bestMatches"
})
public class BestMatchesContainer {
    @JsonProperty("bestMatches")
    public List<BestMatch> bestMatches = null;
}



