package com.aston.homework.service.impl;

import com.aston.homework.dao.DAOException;
import com.aston.homework.dao.UserDAO;
import com.aston.homework.entity.User;
import com.aston.homework.service.UserService;
import com.aston.homework.service.UserServiceException;
import com.aston.homework.service.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserDAO userDAO;
    private final UserValidator userValidator = new UserValidator();

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getUserById(int id) throws UserServiceException {
        logger.info("Finding user by id: {}", id);
        userValidator.validateId(id);
        try {
            return userDAO.findUserById(id).orElseThrow(() -> new UserServiceException("user not found"));
        } catch (DAOException e) {
            throw new UserServiceException(e);
        }
    }

    public User addUser(User user) throws UserServiceException {
        logger.info("Saving new user");
        userValidator.validateData(user);
        userValidator.normalizeEmail(user);
        try {
            if (userDAO.existsByEmail(user.getEmail())) {
                logger.info("saving user is unavailable, such email exists");
                throw new UserServiceException("saving is unavailable, such email exists");
            }
            return userDAO.saveUser(user);
        } catch (DAOException e) {
            throw new UserServiceException(e);
        }
    }

    public boolean updateUserById(int id, String newName, String newEmail, int newAge) throws UserServiceException {
        logger.info("Updating user with id={}", id);
        userValidator.validateId(id);
        userValidator.validateData(newName, newEmail, newAge);
        newEmail = userValidator.normalizeEmail(newEmail);

        try {
            User userForUpdate = userDAO.findUserById(id).orElseThrow(() ->
                    new UserServiceException("user with id=%d not found".formatted(id)));
            if(!newEmail.equals(userForUpdate.getEmail()) && userDAO.existsByEmail(newEmail)) {
                logger.info("this email already used");
                throw new UserServiceException("this email already used");
            }
            userForUpdate.setName(newName);
            userForUpdate.setEmail(newEmail);
            userForUpdate.setAge(newAge);
            return userDAO.updateUser(userForUpdate);
        } catch (DAOException e) {
            throw new UserServiceException(e);
        }
    }

    public boolean deleteUser(int id) throws UserServiceException {
        logger.info("Deleting user with id={}", id);
        userValidator.validateId(id);
        try {
            return userDAO.deleteUser(id);
        } catch (DAOException e) {
            throw new UserServiceException(e);
        }
    }

    public User createUser(String name, String email, int age) throws UserServiceException {
        logger.info("Creating new user with name={}, email={}, age={}", name, email, age);
        userValidator.validateData(name, email, age);
        return new User(name, email, age);
    }
}
