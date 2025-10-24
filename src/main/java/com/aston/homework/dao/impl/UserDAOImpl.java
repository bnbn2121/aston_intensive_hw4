package com.aston.homework.dao.impl;

import com.aston.homework.dao.DAOException;
import com.aston.homework.dao.UserDAO;
import com.aston.homework.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private final SessionFactory sessionFactory;

    public UserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findUserById(int id) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);
            if (user == null) {
                logger.info("user is not founded in DB");
            } else {
                logger.info("user founded in DB successfully");
            }
            return Optional.ofNullable(user);
        } catch (HibernateException e) {
            logger.error("error finding user in DB");
            throw new DAOException("error finding user", e);
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT u FROM User u WHERE u.email = :email";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("email", email.toLowerCase());
            Optional<User> optionalUser = query.uniqueResultOptional();
            if (optionalUser.isPresent()) {
                logger.info("user founded in DB with id={}", optionalUser.get().getId());
            } else {
                logger.info("user is not founded in DB");
            }
            return optionalUser;
        } catch (HibernateException e) {
            logger.error("error finding user in DB");
            throw new DAOException("error finding user", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws DAOException {
        return findUserByEmail(email).isPresent();
    }

    @Override
    public User saveUser(User user) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                logger.info("saving user in DB is success");
                return user;
            } catch (HibernateException e) {
                transaction.rollback();
                logger.error("error saving user in DB: {}", e.getMessage());
                throw new DAOException("error saving user", e);
            }
        }
    }

    @Override
    public boolean updateUser(User user) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User userFromDB = session.find(User.class, user.getId());
                if (userFromDB == null) {
                    transaction.rollback();
                    logger.info("user is not founded in DB");
                    throw new DAOException("user not found");
                }
                userFromDB.setName(user.getName());
                userFromDB.setEmail(user.getEmail());
                userFromDB.setAge(user.getAge());
                transaction.commit();
                logger.info("updating user in DB is success");
                return true;
            } catch (HibernateException e) {
                transaction.rollback();
                logger.error("error updating user in DB: {}", e.getMessage());
                throw new DAOException("error updating user", e);
            }
        }
    }

    @Override
    public boolean deleteUser(int id) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User userFromDB = session.find(User.class, id);
                if (userFromDB == null) {
                    transaction.rollback();
                    logger.info("user is not founded in DB");
                    throw new DAOException("user not found");
                }
                session.remove(userFromDB);
                transaction.commit();
                logger.info("deleting user from DB is success");
                return true;
            } catch (HibernateException e) {
                transaction.rollback();
                logger.error("error deleting user from DB: {}", e.getMessage());
                throw new DAOException("error deleting user", e);
            }
        }
    }
}

