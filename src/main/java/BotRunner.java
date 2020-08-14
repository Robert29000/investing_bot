import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;



public class BotRunner {


    public static void main(String[] argsp){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        String token = System.getenv("BOT_TOKEN");
        try{
            telegramBotsApi.registerBot(new InvestingBot(token));
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }

}
