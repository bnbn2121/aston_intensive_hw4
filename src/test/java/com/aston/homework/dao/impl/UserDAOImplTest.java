package com.aston.homework.dao.impl;

import com.aston.homework.dao.DAOException;
import com.aston.homework.dao.UserDAO;
import com.aston.homework.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDAOImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");
    private SessionFactory sessionFactory;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate-test.cfg.xml");

        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());

        sessionFactory = configuration.buildSessionFactory();
        userDAO = new UserDAOImpl(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void shouldSaveUser() throws Exception{
        // Given
        User user = new User("testUser", "test@mail.ru", 25);

        // When
        userDAO.saveUser(user);

        // Then
        assertEquals(1, user.getId());
        assertEquals("testUser", user.getName());
        assertEquals("test@mail.ru", user.getEmail());
        assertEquals(25, user.getAge());
        assertNotNull(user.getCreatedAt());
    }

    private User presaveUserInDB() throws DAOException {
        User presavedUser = new User("testUser", "test@mail.ru", 25);
        return userDAO.saveUser(presavedUser);
    }

    @Test
    void shouldFindUserById() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUser = userDAO.findUserById(presavedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(presavedUser.getId(), foundUser.get().getId());
        assertEquals(presavedUser.getName(), foundUser.get().getName());
        assertEquals(presavedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(presavedUser.getAge(), foundUser.get().getAge());
        assertEquals(presavedUser.getCreatedAt(), foundUser.get().getCreatedAt());
    }

    @Test
    void shouldFindUserByNotExistsId() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUserNotExistsId = userDAO.findUserById(presavedUser.getId()+1);

        // Then
        assertTrue(foundUserNotExistsId.isEmpty());
    }

    @Test
    void shouldFindUserByEmail() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUser = userDAO.findUserByEmail(presavedUser.getEmail());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(presavedUser.getId(), foundUser.get().getId());
        assertEquals(presavedUser.getName(), foundUser.get().getName());
        assertEquals(presavedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(presavedUser.getAge(), foundUser.get().getAge());
        assertEquals(presavedUser.getCreatedAt(), foundUser.get().getCreatedAt());
    }

    @Test
    void shouldFindUserByNotExistsEmail() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUserNotExistsEmail = userDAO.findUserByEmail("notExistEmail@mail.ru");

        // Then
        assertTrue(foundUserNotExistsEmail.isEmpty());
    }

    @Test
    void shouldExistsByEmail() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        // When
        boolean existsEmail = userDAO.existsByEmail(presavedUser.getEmail());
        boolean notExistsEmail = userDAO.existsByEmail("notExistsEmial@mail.ru");

        // Then
        assertTrue(existsEmail);
        assertFalse(notExistsEmail);
    }

    @Test
    void shouldDeleteUser() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        // When
        Optional<User> foundUserBeforeDelete = userDAO.findUserById(presavedUser.getId());
        boolean isUserDeleted = userDAO.deleteUser(presavedUser.getId());
        Optional<User> foundUserAfterDelete = userDAO.findUserById(presavedUser.getId());

        // Then
        assertTrue(foundUserBeforeDelete.isPresent());
        assertTrue(isUserDeleted);
        assertTrue(foundUserAfterDelete.isEmpty());
    }

    @Test
    void shouldDeleteNotExistsUser() throws Exception{
        // Given
        int id = 8;

        // When & Then
        assertThrows(DAOException.class, () -> userDAO.deleteUser(id));
    }

    @Test
    void shouldUpdateUser() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();

        String nameBeforeUpdate = presavedUser.getName();
        String emailBeforeUpdate = presavedUser.getEmail();
        int ageBeforeUpdate = presavedUser.getAge();

        String nameAfterUpdate = "newName";
        String emailAfterUpdate = "newEmail@mail.ru";
        int ageAfterUpdate = 50;

        // When
        presavedUser.setName(nameAfterUpdate);
        presavedUser.setEmail(emailAfterUpdate);
        presavedUser.setAge(ageAfterUpdate);
        boolean isUserUpdated = userDAO.updateUser(presavedUser);
        Optional<User> foundUserAfterUpdate = userDAO.findUserById(presavedUser.getId());

        // Then
        assertTrue(isUserUpdated);
        assertTrue(foundUserAfterUpdate.isPresent());
        assertEquals(presavedUser.getId(), foundUserAfterUpdate.get().getId());
        assertEquals(nameAfterUpdate, foundUserAfterUpdate.get().getName());
        assertEquals(emailAfterUpdate, foundUserAfterUpdate.get().getEmail());
        assertEquals(ageAfterUpdate, foundUserAfterUpdate.get().getAge());
        assertEquals(presavedUser.getCreatedAt(), foundUserAfterUpdate.get().getCreatedAt());
    }

    @Test
    void shouldUpdateNotExistsUser() throws Exception{
        // Given
        User presavedUser = presaveUserInDB();
        userDAO.deleteUser(presavedUser.getId());

        // When & Then
        DAOException exception = assertThrows(DAOException.class, () -> userDAO.updateUser(presavedUser));
        assertEquals("user not found", exception.getMessage());
    }
}
