import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegram.Bot;


public class App {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot("RateForecast_bot"
                    , "5245239563:AAFRQX8D7hMuiX2VuW29Q1-22K4gLGM0pZM"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}