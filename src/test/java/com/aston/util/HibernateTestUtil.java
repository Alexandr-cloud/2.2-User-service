package com.aston.util;

import com.aston.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateTestUtil {

    public static SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url",
                System.getProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/test"));
        configuration.setProperty("hibernate.connection.username",
                System.getProperty("hibernate.connection.username", "postgres"));
        configuration.setProperty("hibernate.connection.password",
                System.getProperty("hibernate.connection.password", ""));

        return configuration.buildSessionFactory();
    }
}
