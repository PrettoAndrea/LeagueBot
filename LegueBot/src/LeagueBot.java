import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.*;

public class LeagueBot extends TelegramLongPollingBot {


    @Override
    public String getBotUsername() {
        return "LeagueGuidaBot";
    }

    @Override
    public String getBotToken() {
        return "8090086093:AAH_fAn9J-rFoBptQwBySBgd3FhhI1cb8mE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (messageText.startsWith("/guida")) {
                String campione = messageText.replace("/guida", "").trim();
                String risposta = getGuide(campione);

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(risposta);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getGuide(String nomeCampione) {
        String response = "Guida non trovata per il campione: " + nomeCampione;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LeagueDB", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.Nome, c.Ruolo, b.Rune, b.Oggetti, b.Abilità " +
                             "FROM Campioni c JOIN Build b ON c.ID_Campione = b.ID_Campione WHERE c.Nome = ?")) {
            stmt.setString(1, nomeCampione);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                response = String.format(
                        "Guida per %s (%s):\n- Rune: %s\n- Oggetti: %s\n- Priorità Abilità: %s",
                        rs.getString("Nome"), rs.getString("Ruolo"), rs.getString("Rune"),
                        rs.getString("Oggetti"), rs.getString("Abilità")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }
}
