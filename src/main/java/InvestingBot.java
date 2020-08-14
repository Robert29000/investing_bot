import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import static java.lang.Math.toIntExact;


public class InvestingBot extends TelegramLongPollingBot {

    private static String TOKEN = "1372192675:AAFfS7smwc5NnxU-SqBt1tZQm5V0Kll81CE";
    private static final String USERNAME = "investinginformation_bot";
    private static final String helpString = "Этот бот поможет Вам посмотреть " +
            "самую актуальную информацию о рынках. " +
            "Для начала работы введите команду /start , дальше выберете интересующий рынок и " +
            "и введите название интересующего инструмента.";
    private static final String wrongString = "Для помощи наберите команду /help , " +
            "для выбора рынка укажите один из предложенных: фондовый или форекс.";
    private static final String startString = "Для начала работы выберете рынок, который Вас интересует.";
    private static final String stockString = "Напишите название интересующей акции и выберете подходящую.";
    private static final String chooseStockString = "Нажмите на компанию, которую Вы искали.";
    private static final String forexString = "Напишите названия валют в формате: (Из какой) - (В какую)\n" +
            "Например: USD - RUB";
    private static final String incorrectForexFormatString = "Для корректной работы" +
            " бота используйте данный формат: (Из какой) - (В какую). Коды валюты пишите на английском языке";
    private static final String incorrectStockFormatString = "Для корректной работы боты пишите " +
            "существующие компании. Их названия должны содержать только английские буквы";
    private static final String errorStockString = "Произошла ошибка на сервере, попробуйте через другое время.";

    enum Market{
        Stock,
        Forex,
        None
    }

    Market choosenMarket = Market.None;

    InvestingBot(String token){
        super();
        TOKEN = token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (message.equals("/start")) {
                sendStartMsg(chatId);
                choosenMarket = Market.None;
            } else if (message.equals("/help")) {
                sendMessage(chatId, helpString);
                choosenMarket = Market.None;
            } else if (choosenMarket != Market.None) {
                switch (choosenMarket) {
                    case Stock:
                        List<BestMatch> results = AlphavantageConnector.startStockSearch(message);
                        if (results == null || results.isEmpty()){
                            sendMessage(chatId, incorrectStockFormatString);
                            break;
                        }
                        sendStocksMessage(chatId, results);
                        break;
                    case Forex:
                        int pos = message.indexOf('-');
                        if (pos == -1){
                            sendMessage(chatId, incorrectForexFormatString);
                            break;
                        }
                        String from = message.substring(0, pos - 1);
                        String to = message.substring(pos + 2);
                        RealtimeCurrencyExchangeRate result = AlphavantageConnector.
                                getForexInformation(from, to);
                        if (result == null){
                            sendMessage(chatId, incorrectForexFormatString);
                            break;
                        }
                        sendForexMessage(chatId, result);
                        break;
                    default:
                        break;
                }
            } else if (message.equals("Фондовый")) {
                choosenMarket = Market.Stock;
                sendMessage(chatId, stockString);
            } else if (message.equals("Форекс")) {
                choosenMarket = Market.Forex;
                sendMessage(chatId, forexString);
            } else {
                sendMessage(chatId, wrongString);
            }
        }else if (update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            StockInfo stock = AlphavantageConnector.getStockInformation(callbackData);
            if (stock == null){
                sendMessage(Long.toString(chatId), errorStockString);
            }else {
                sendEditedMessage(stock, messageId, chatId);
            }
        }
    }


    public synchronized void setInvestingButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("Фондовый"));
        firstRow.add(new KeyboardButton("Форекс"));


        keyboard.add(firstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void setInlineStocksButtons(SendMessage sendMessage, List<BestMatch> matches){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (BestMatch matchResult : matches){
            List<InlineKeyboardButton> singleRow = new ArrayList<>();
            singleRow.add(new InlineKeyboardButton().
                    setText(matchResult.name).setCallbackData(matchResult.symbol));
            rowsInLine.add(singleRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    public synchronized String getTextFromForex(RealtimeCurrencyExchangeRate exchangeRate){
        String res;
        res = "Код первой валюты: " + exchangeRate.fromCurrencyCode + "\n";
        res += "Название первой валюты: " + exchangeRate.fromCurrencyName + "\n";
        res += "Код второй валюты: " + exchangeRate.toCurrencyCode + "\n";
        res += "Название второй валюты: " + exchangeRate.toCurrencyName + "\n";
        res += "Курс: " + exchangeRate.exchangeRate + "\n";
        res += "Последнее обновление: " + exchangeRate.lastRefreshed + "\n";
        res += "Временная зона: " + exchangeRate.timeZone + "\n";
        res += "Цена предложения: " + exchangeRate.bidPrice + "\n";
        res += "Цена продажи: " + exchangeRate.askPrice + "\n";
        return res;
    }

    public synchronized String getTextFromStock(StockInfo stock){
        String res;
        res = "Обозначение: " + stock.symbol + "\n";
        res += "Открытие: " + stock.open + "\n";
        res += "Наибольшее зн-е: " + stock.high + "\n";
        res += "Наименьшее зн-е: " + stock.low + "\n";
        res += "Текущее зн-е: " + stock.price + "\n";
        res += "Объем: " + stock.volume + "\n";
        res += "Последний день торгов: " + stock.latestTradingDay + "\n";
        res += "Предыдущее торговое зн-е: " + stock.previousClose + "\n";
        res += "Изменение: " + stock.change + "\n";
        res += "Изменение в процентах: " + stock.changePercent;
        return res;
     }

    public synchronized void sendEditedMessage(StockInfo stock, long messageId, long chatId){
        EditMessageText changedMessage = new EditMessageText();
        changedMessage.setChatId(chatId).
                setMessageId(toIntExact(messageId)).
                setText(getTextFromStock(stock));
        try{
            execute(changedMessage);
        }catch (TelegramApiException e){
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    public synchronized void sendStartMsg(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(startString);
        setInvestingButtons(sendMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    public synchronized void sendForexMessage(String chatId, RealtimeCurrencyExchangeRate exchangeRate){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(getTextFromForex(exchangeRate));
        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    public synchronized void sendStocksMessage(String chatId, List<BestMatch> matches){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(chooseStockString);
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        setInlineStocksButtons(sendMessage, matches);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    public void sendMessage(String chatId, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
        sendMessage.setReplyMarkup(removeKeyboard);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Logger log = Logger.getLogger(BotRunner.class.getName());
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    @Override
    public String getBotToken(){
        return TOKEN;
    }

    @Override
    public String getBotUsername(){
        return USERNAME;
    }
}
