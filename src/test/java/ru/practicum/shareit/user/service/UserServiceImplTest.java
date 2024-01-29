package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exсeption.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getAllUsersTest() {
        long userId = 1L;
        User oldUser = new User();
        oldUser.setEmail("oldUser@uandex.ru");
        oldUser.setName("userId");
        oldUser.setId(userId);
        List<User> userList = new ArrayList<>();
        userList.add(oldUser);
        when(userRepository.findAll()).thenReturn(userList);
        int sizeListUsers = userService.getAllUsers().size();
        assertEquals(1, sizeListUsers, "Метод отработал некорректно");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUserTest_whenUserNameValid_thenSavedUser() {
        User userToSave = new User();
        when(userRepository.save(userToSave))
                .thenReturn(userToSave);
        User actualUser = userService.createUser(userToSave);

        assertEquals(userToSave, actualUser, "Метод отработал некорректно");
        verify(userRepository).save(userToSave);
        verify(userRepository, times(1)).save(userToSave);
        verify(userRepository, atMost(1)).save(userToSave);
    }


    @Test
    void updateUser_whenUserFound_thenUpdateUser() {
        long userId = 1L;
        User oldUser = new User();
        oldUser.setEmail("oldUser@uandex.ru");
        oldUser.setName("userId");
        oldUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        User newUser = new User();
        newUser.setEmail("newUser@uandex.ru");
        newUser.setName("newUser");
        newUser.setId(userId);

        oldUser = userService.updateUser(newUser);
        assertEquals(oldUser, newUser);
        verify(userRepository).save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();
        assertEquals("newUser@uandex.ru", actualUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenReturnThrows() {
        long userId = 0L;
        User oldUser = new User();
        oldUser.setEmail("oldUser@uandex.ru");
        oldUser.setName("userId");
        when(userRepository.findById(userId))
                .thenThrow(NotFoundException.class);

        User newUser = new User();
        newUser.setEmail("newUser@uandex.ru");
        newUser.setName("newUser");
        assertThrows(NotFoundException.class, () -> userService.updateUser(newUser));
        verify(userRepository, never()).save(newUser);
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        long userId = 0L;
        User oldUser = new User();
        oldUser.setEmail("oldUser@uandex.ru");
        oldUser.setName("userId");
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        User user = userService.getUserById(userId);
        assertEquals(oldUser, user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenUserNotFound_thenReturnThrows() {
        long userId = 0L;
        User oldUser = new User();
        oldUser.setEmail("oldUser@uandex.ru");
        oldUser.setName("userId");
        when(userRepository.findById(userId))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteUser_whenUserFound_thenDeleteUser() {
        User user = new User(1L, "oldUser@uandex.ru", "userId");
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}
