import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Realtime Currency Exchange Rate"
})
public class ForexResult {

    @JsonProperty("Realtime Currency Exchange Rate")
    public RealtimeCurrencyExchangeRate realtimeCurrencyExchangeRate;
}
