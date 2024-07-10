package javaapplication;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebService
public class UserService {

    @WebMethod
    public String login(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "Login successful";
            } else {
                return "Login failed";
            }
        } catch (SQLException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/user", new UserService());
        System.out.println("User Service is running...");
    }
}
