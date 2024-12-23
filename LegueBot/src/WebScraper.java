import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.sql.*;

public class WebScraper {

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:8081/LeagueDB", "root", "")) {
            Document doc = Jsoup.connect("https://www.op.gg/champions").get();

            for (Element champ : doc.select(".champion-index__champion-item")) {
                String nome = champ.select(".champion-index__champion-item__name").text();
                String ruolo = champ.select(".champion-index__champion-item__position").text();

                // Dati fittizi
                String rune = "Conqueror, Triumph, Legend: Tenacity, Coup de Grace";
                String oggetti = "Infinity Edge, Immortal Shieldbow";
                String abilita = "Q > E > W";

                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Campioni (Nome, Ruolo) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nome);
                    stmt.setString(2, ruolo);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int idCampione = rs.getInt(1);
                        try (PreparedStatement buildStmt = conn.prepareStatement(
                                "INSERT INTO Build (ID_Campione, Rune, Oggetti, Abilità) VALUES (?, ?, ?, ?)")) {
                            buildStmt.setInt(1, idCampione);
                            buildStmt.setString(2, rune);
                            buildStmt.setString(3, oggetti);
                            buildStmt.setString(4, abilita);
                            buildStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
