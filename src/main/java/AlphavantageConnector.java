import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlphavantageConnector {
    private static final String ALPHA_TOKEN = "FU3QLJV6JDBJK3SJ";


    public static RealtimeCurrencyExchangeRate getForexInformation(String from, String to){
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpUriRequest httpGet = new HttpGet("https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency="+from+"&to_currency="+to+"&apikey="
                +ALPHA_TOKEN);
        try{
           CloseableHttpResponse response = httpClient.execute(httpGet);
           final HttpEntity entity = response.getEntity();
           String jsonObject = EntityUtils.toString(entity);

           StringReader stringReader = new StringReader(jsonObject);
           ObjectMapper mapper = new ObjectMapper();

           ForexResult res = mapper.readValue(stringReader, ForexResult.class);

           return res.realtimeCurrencyExchangeRate;
        }catch (IOException e){
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
            return null;
        }
    }

    public static StockInfo getStockInformation(String symbol){
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpUriRequest httpGet = new HttpGet("https://www.alphavantage.co/query?function=GLOBAL_QUOTE" +
                "&symbol="+symbol+"&apikey="+ALPHA_TOKEN);
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            final HttpEntity entity = response.getEntity();
            String jsonObject = EntityUtils.toString(entity);

            StringReader stringReader = new StringReader(jsonObject);
            ObjectMapper mapper = new ObjectMapper();

            GlobalQuote globalQuote = mapper.readValue(stringReader, GlobalQuote.class);

            return globalQuote.stock;
        }catch (IOException e){
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
            return null;
        }
    }


    public static List<BestMatch> startStockSearch(String message){
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpUriRequest httpGet = new HttpGet("" +
                "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords="+message+"&apikey="
                + ALPHA_TOKEN);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            final HttpEntity entity = response.getEntity();
            String jsonObject = EntityUtils.toString(entity);

            StringReader stringReader = new StringReader(jsonObject);
            ObjectMapper mapper = new ObjectMapper();

            BestMatchesContainer res = mapper.readValue(stringReader, BestMatchesContainer.class);

            return res.bestMatches;

        }catch (IOException e){
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
            return null;
        }

    }
}
