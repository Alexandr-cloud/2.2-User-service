package com.aston;

import com.aston.console.ConsoleMenu;
import com.aston.dao.UserDao;
import com.aston.dao.impl.UserDaoImpl;
import com.aston.service.UserService;
import com.aston.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        UserDao userDao = new UserDaoImpl(factory);
        UserService userService = new UserService(userDao);
        ConsoleMenu menu = new ConsoleMenu(userService);
        menu.start();
    }
}