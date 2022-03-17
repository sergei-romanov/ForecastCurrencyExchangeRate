package telegram;

import controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class Bot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);
    private final String botName;
    private final String botToken;

    public Bot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOG.debug("получен новый update. updateID: " + update.getUpdateId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            String chat_id = update.getMessage().getChatId().toString();
            Controller controller = ParseMessage.parse(message_text);
            String responce = controller.operate();
            if (responce.equals("chart")) {
                SendPhoto image = new SendPhoto();
                image.setChatId(chat_id);
                image.setPhoto(new InputFile(new File("src/main/resources/XYLineChart.jpeg")));
                try {
                    execute(image);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessage message = new SendMessage();
                message.setChatId(chat_id);
                message.setText(responce);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        LOG.debug("Имя бота: " + botName);
        return botName;
    }

    @Override
    public String getBotToken() {
        LOG.debug("Ключ бота: " + botToken);
        return botToken;
    }
}