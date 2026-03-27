import java.sql.*;

public class CheckCandidatures {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/camping_management?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to DB!");
            
            // Check v2 table
            System.out.println("\n--- Table: candidatures_service_v2 ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM candidatures_service_v2")) {
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columns; i++) {
                        System.out.print(md.getColumnName(i) + ": " + rs.getString(i) + " | ");
                    }
                    System.out.println();
                }
            } catch (Exception e) {
                System.out.println("Error reading v2 table: " + e.getMessage());
            }

            // Check event_services
            System.out.println("\n--- Table: event_services ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM event_services LIMIT 5")) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getLong("id") + " | EventID: " + rs.getLong("event_id") + " | Name: " + rs.getString("name"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
