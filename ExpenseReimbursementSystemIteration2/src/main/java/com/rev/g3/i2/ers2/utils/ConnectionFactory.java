package com.rev.g3.i2.ers2.utils;// ConnectionFactory.java

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static ConnectionFactory instance;
    private String url;
    private String username;
    private String password;
    private String forName;

    private ConnectionFactory() {
        Dotenv dotenv = Dotenv.configure().load();
        url = dotenv.get("DATABASE_URL");
        username = dotenv.get("DATABASE_USERNAME");
        password = dotenv.get("DATABASE_PASSWORD") == null ? "" : dotenv.get("DATABASE_PASSWORD");
        forName = dotenv.get("DATABASE_FORNAME");

        if (this.url == null || this.username == null || this.password == null || this.forName == null) {
            throw new IllegalStateException("Critical Error: Database environment variables not configured.");
        }

        try {
            Class.forName(forName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC driver not found on classpath.", e);
        }
    }

    public static synchronized ConnectionFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionFactory();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}