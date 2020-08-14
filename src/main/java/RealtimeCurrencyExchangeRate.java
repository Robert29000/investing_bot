import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "1. From_Currency Code",
        "2. From_Currency Name",
        "3. To_Currency Code",
        "4. To_Currency Name",
        "5. Exchange Rate",
        "6. Last Refreshed",
        "7. Time Zone",
        "8. Bid Price",
        "9. Ask Price"
})
public class RealtimeCurrencyExchangeRate {
    @JsonProperty("1. From_Currency Code")
    public String fromCurrencyCode;
    @JsonProperty("2. From_Currency Name")
    public String fromCurrencyName;
    @JsonProperty("3. To_Currency Code")
    public String toCurrencyCode;
    @JsonProperty("4. To_Currency Name")
    public String toCurrencyName;
    @JsonProperty("5. Exchange Rate")
    public String exchangeRate;
    @JsonProperty("6. Last Refreshed")
    public String lastRefreshed;
    @JsonProperty("7. Time Zone")
    public String timeZone;
    @JsonProperty("8. Bid Price")
    public String bidPrice;
    @JsonProperty("9. Ask Price")
    public String askPrice;
}
