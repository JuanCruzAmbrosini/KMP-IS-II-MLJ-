package com.biblioteca.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.community.dialect.SQLiteDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/** Selecciona MySQL y usa SQLite si MySQL no responde al iniciar. */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Bean
    public DataSource dataSource() {
        HikariDataSource mysql = new HikariDataSource();
        mysql.setJdbcUrl(mysqlUrl);
        mysql.setUsername(mysqlUsername);
        mysql.setPassword(mysqlPassword);
        mysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        mysql.setConnectionTimeout(3000);

        try (Connection ignored = mysql.getConnection()) {
            System.out.println(">>> Base de datos activa: MySQL");
            return mysql;
        } catch (SQLException exception) {
            mysql.close();
            HikariDataSource sqlite = new HikariDataSource();
            sqlite.setJdbcUrl("jdbc:sqlite:biblioteca-fallback.db");
            sqlite.setDriverClassName("org.sqlite.JDBC");
            sqlite.addDataSourceProperty("foreign_keys", "true");
            System.out.println(">>> MySQL no disponible; usando SQLite en biblioteca-fallback.db");
            return sqlite;
        }
    }

    @Bean
    public HibernatePropertiesCustomizer hibernateDialect(DataSource dataSource) {
        return properties -> {
            if (dataSource instanceof HikariDataSource hikari
                    && hikari.getJdbcUrl().startsWith("jdbc:sqlite:")) {
                properties.put("hibernate.dialect", SQLiteDialect.class.getName());
            } else {
                properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            }
        };
    }
}