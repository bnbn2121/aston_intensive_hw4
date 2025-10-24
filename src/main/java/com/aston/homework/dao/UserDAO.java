package com.aston.homework.dao;

import com.aston.homework.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findUserById(int id) throws DAOException;
    Optional<User> findUserByEmail(String email) throws DAOException;
    User saveUser(User user) throws DAOException;
    boolean updateUser(User user) throws DAOException;
    boolean deleteUser(int id) throws DAOException;
    boolean existsByEmail(String email) throws DAOException;
}
