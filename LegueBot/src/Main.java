public class Main {
    public static void main(String[] args) {
        try {

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);


            telegramBotsApi.registerBot(new LeagueBot());

            System.out.println("Bot avviato con successo!");
        } catch (TelegramApiException e) {
            System.err.println("Errore durante l'avvio del bot: " + e.getMessage());
        }
    }
}
