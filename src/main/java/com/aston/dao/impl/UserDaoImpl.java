package com.aston.dao.impl;

import com.aston.dao.UserDao;
import com.aston.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved : {}" , user);
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to save user" , e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Failed to find User by id : {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User" , User.class).list();
        } catch (Exception e) {
            logger.error("Failed to fetch all users ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User merged = session.merge(user);
            transaction.commit();
            logger.info("User update : {}" , merged);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.error("Failed to update user" , e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            User user = session.get(User.class , id);
            transaction = session.beginTransaction();
            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
            logger.info("User delete : {}" , user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                logger.error("Failed to delete user" , e);
                throw new RuntimeException(e);
            }
        }
    }
}