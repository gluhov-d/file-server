package com.github.gluhov.service.user;

import com.github.gluhov.model.User;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.service.UserService;
import com.github.gluhov.to.UserTo;
import com.github.gluhov.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.github.gluhov.service.user.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void getById() {
        when(userRepository.getById(USER_ID)).thenReturn(Optional.of(user1));
        Optional<UserTo> result = userService.getById(USER_ID);
        assertTrue(result.isPresent());
        assertEquals(UserUtil.createTo(user1), result.get());
    }

    @Test
    public void getByIdNotFound() {
        when(userRepository.getById(USER_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<UserTo> result = userService.getById(USER_NOT_FOUND_ID);
        assertFalse(result.isPresent());
    }

    @Test
    public void deleteById() {
        doNothing().when(userRepository).deleteById(USER_ID+1);
        userService.deleteById(USER_ID+1);
        verify(userRepository, times(1)).deleteById(USER_ID+1);
    }

    @Test
    public void save() {
        when(userRepository.save(any(User.class))).thenReturn(Optional.of(user3));
        Optional<User> result = userService.save(user3);
        assertTrue(result.isPresent());
        assertEquals(user3, result.get());
    }

    @Test
    public void update() {
        when(userRepository.update(any(User.class))).thenReturn(UserTestData.getUpdated());

        Optional<User> result = userService.update(UserTestData.getUpdated().get());

        assertTrue(result.isPresent());
        assertEquals(UserTestData.getUpdated(), result);
    }
}