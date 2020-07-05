package se.lexicon.MartinKlasson.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ToDoItDataSource {

    static final String URL = "jdbc:mysql://localhost:3306/todoit?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Berlin";
    static final String USER = "root";
    static final String PASSWORD = "JavaGroup30MartinKlasson";

    public static Connection getConnection() throws SQLException {
        return  DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
