package com.sv.grupo3.restaurante.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {

    private static HikariDataSource dataSource;

    private DataSourceConfig() {}

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig c = new HikariConfig();

            // Conexión (1-4)
            c.setJdbcUrl(env("DB_URL", "jdbc:mysql://localhost:3306/restaurante"));
            c.setUsername(env("DB_USER", "root"));
            c.setPassword(env("DB_PASSWORD", ""));
            c.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Identificación del pool (5)
            c.setPoolName("RestaurantePool");

            // Tamaño del pool (6-7)
            c.setMaximumPoolSize(10);
            c.setMinimumIdle(2);

            // Tiempos (8-12)
            c.setConnectionTimeout(30_000);
            c.setIdleTimeout(600_000);
            c.setMaxLifetime(1_800_000);
            c.setValidationTimeout(5_000);
            c.setLeakDetectionThreshold(60_000);

            // Comportamiento (13-15)
            c.setAutoCommit(true);
            c.setConnectionTestQuery("SELECT 1");
            c.setInitializationFailTimeout(1);

            dataSource = new HikariDataSource(c);
        }
        return dataSource;
    }

    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}