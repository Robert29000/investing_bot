import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "01. symbol",
        "02. open",
        "03. high",
        "04. low",
        "05. price",
        "06. volume",
        "07. latest trading day",
        "08. previous close",
        "09. change",
        "10. change percent"
})
public class StockInfo {
    @JsonProperty("01. symbol")
    public String symbol;
    @JsonProperty("02. open")
    public String open;
    @JsonProperty("03. high")
    public String high;
    @JsonProperty("04. low")
    public String low;
    @JsonProperty("05. price")
    public String price;
    @JsonProperty("06. volume")
    public String volume;
    @JsonProperty("07. latest trading day")
    public String latestTradingDay;
    @JsonProperty("08. previous close")
    public String previousClose;
    @JsonProperty("09. change")
    public String change;
    @JsonProperty("10. change percent")
    public String changePercent;
}
