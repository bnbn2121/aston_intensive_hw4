package com.aston.homework.service.impl;

import com.aston.homework.dao.DAOException;
import com.aston.homework.dao.UserDAO;
import com.aston.homework.entity.User;
import com.aston.homework.service.UserServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private UserServiceImpl userService;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    void shouldCreateUser() throws Exception {
        // Given
        String name = "testUser";
        String email = "test@mail.ru";
        int age = 25;

        // When
        User user = userService.createUser(name, email, age);

        // Then
        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void shouldGetUserByExistsId() throws Exception {
        // Given
        int userId = 1;
        User user = new User("testUser", "test@mail.ru", 25);

        when(userDAO.findUserById(userId)).thenReturn(Optional.of(user));

        // When
        User userObtained = userService.getUserById(userId);

        // Then
        assertNotNull(userObtained);
        assertEquals("testUser", userObtained.getName());
        assertEquals("test@mail.ru", userObtained.getEmail());
        assertEquals(25, userObtained.getAge());
        verify(userDAO).findUserById(userId);
    }

    @Test
    void shouldGetUserByNotExistsId() throws Exception {
        // Given
        int userId = 999;

        when(userDAO.findUserById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.getUserById(userId));
        assertEquals("user not found", exception.getMessage());
        verify(userDAO).findUserById(userId);
    }

    @Test
    void shouldGetUserByInvalidId() throws Exception {
        // Given
        int userId = -1;

        // When & Then
        assertThrows(UserServiceException.class, () -> userService.getUserById(userId));
        verify(userDAO, never()).findUserById(anyInt());
    }

    @Test
    void shouldAddUserWithNotExistsEmail() throws Exception {
        // Given
        User user = new User("testUser", "test@mail.ru", 25);

        when(userDAO.existsByEmail(user.getEmail())).thenReturn(false);
        when(userDAO.saveUser(user)).thenReturn(user);

        // When
        User userAfterAdd = userService.addUser(user);

        // Then
        assertNotNull(userAfterAdd);
        assertEquals("testUser", userAfterAdd.getName());
        assertEquals("test@mail.ru", userAfterAdd.getEmail());
        assertEquals(25, userAfterAdd.getAge());
        verify(userDAO).existsByEmail(user.getEmail());
        verify(userDAO).saveUser(user);
    }

    @Test
    void shouldAddUserWithExistsEmail() throws Exception {
        // Given
        User user = new User("testUser", "test@mail.ru", 25);

        when(userDAO.existsByEmail(user.getEmail())).thenReturn(true);

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.addUser(user));
        assertEquals("saving is unavailable, such email exists", exception.getMessage());
        verify(userDAO).existsByEmail(user.getEmail());
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void shouldUpdateUserWithNotExistsEmail() throws Exception {
        // Given
        int id = 1;
        User userForUpdate = new User("testUser", "test@mail.ru", 25);
        String newName = "new name";
        String newEmail = "new@email.com";
        int newAge = 50;

        when(userDAO.findUserById(id)).thenReturn(Optional.of(userForUpdate));
        when(userDAO.existsByEmail(newEmail)).thenReturn(false);
        when(userDAO.updateUser(userForUpdate)).thenReturn(true);

        // When
        boolean result = userService.updateUserById(id, newName, newEmail, newAge);

        // Then
        assertTrue(result);
        assertEquals(newName, userForUpdate.getName());
        assertEquals(newEmail, userForUpdate.getEmail());
        assertEquals(newAge, userForUpdate.getAge());
        verify(userDAO).findUserById(id);
        verify(userDAO).existsByEmail(newEmail);
        verify(userDAO).updateUser(userForUpdate);
    }

    @Test
    void shouldUpdateUserWithExistsEmail() throws Exception {
        // Given
        int id = 1;
        User userForUpdate = new User("testUser", "test@mail.ru", 25);
        String newName = "new name";
        String newEmail = "new@email.com";
        int newAge = 50;

        when(userDAO.findUserById(id)).thenReturn(Optional.of(userForUpdate));
        when(userDAO.existsByEmail(newEmail)).thenReturn(true);

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.updateUserById(id, newName, newEmail, newAge));
        assertEquals("this email already used", exception.getMessage());

        verify(userDAO).findUserById(id);
        verify(userDAO).existsByEmail(newEmail);
        verify(userDAO, never()).updateUser(any(User.class));
    }

    @Test
    void shouldUpdateUserWithNotExistsUser() throws Exception {
        // Given
        int id = 999;
        User userForUpdate = new User("testUser", "test@mail.ru", 25);
        String newName = "new name";
        String newEmail = "new@email.com";
        int newAge = 50;

        when(userDAO.findUserById(id)).thenReturn(Optional.empty());

        // When & Then
        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.updateUserById(id, newName, newEmail, newAge));
        assertEquals("user with id=%d not found".formatted(id), exception.getMessage());

        verify(userDAO).findUserById(id);
        verify(userDAO, never()).existsByEmail(anyString());
        verify(userDAO, never()).updateUser(any(User.class));
    }

    @Test
    void shouldDeleteUserWithNotExistsId() throws Exception {
        // Given
        int id = 999;

        when(userDAO.deleteUser(id)).thenThrow(DAOException.class);

        // When & Then
        assertThrows(UserServiceException.class, () -> userService.deleteUser(id));

        verify(userDAO).deleteUser(id);
    }

    @Test
    void shouldDeleteUserWithExistsId() throws Exception {
        // Given
        int id = 1;

        when(userDAO.deleteUser(id)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(id);

        // Then
        assertTrue(result);
        verify(userDAO).deleteUser(id);
    }

}
