package com.github.gluhov.service.file;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.FileEntity;
import com.github.gluhov.model.FileStatus;
import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.repository.FileEntityRepository;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.service.FileEntityService;
import com.github.gluhov.to.FileTo;
import com.github.gluhov.util.FileUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.github.gluhov.service.event.EventTestData.event3;
import static com.github.gluhov.service.file.FileTestData.*;
import static com.github.gluhov.service.user.UserTestData.USER_NOT_FOUND_ID;
import static com.github.gluhov.service.user.UserTestData.user3;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class FileEntityServiceTest {

    @Mock
    private FileEntityRepository fileEntityRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;

    @InjectMocks
    private FileEntityService fileEntityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
    }

    @Test
    public void getById() {
        when(fileEntityRepository.getById(FILE_ID)).thenReturn(Optional.of(file1));
        Optional<FileEntity> result = fileEntityService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals(file1, result.get());
    }

    @Test
    public void getByIdNotFound() {
        when(fileEntityRepository.getById(FILE_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<FileEntity> result = fileEntityService.getById(FILE_NOT_FOUND_ID);

        assertFalse(result.isPresent());
    }

    @Test
    public void deleteById() {
        doNothing().when(fileEntityRepository).deleteById(anyLong());

        fileEntityService.deleteById(FILE_ID+1);

        verify(fileEntityRepository, times(1)).deleteById(FILE_ID+1);
    }

    @Test
    public void save() {
        when(fileEntityRepository.save(any(FileEntity.class))).thenReturn(Optional.of(file1));
        when(userRepository.getById(anyLong())).thenReturn(Optional.of(user3));
        when(eventRepository.save(any(Event.class))).thenReturn(Optional.of(event3));
        Optional<FileEntity> result = fileEntityService.save(file1, user3.getId());
        assertTrue(result.isPresent());
        assertEquals(file1, result.get());
        // TODO: Not invoke transaction commit
//        verify(transaction, times(1)).commit();
    }

    @Test
    public void saveWithInvalidUser() {
        when(fileEntityRepository.save(any(FileEntity.class))).thenReturn(Optional.of(file1));
        when(userRepository.getById(anyLong())).thenReturn(Optional.empty());
        Optional<FileEntity> result = fileEntityService.save(file1, USER_NOT_FOUND_ID);
        assertFalse(result.isPresent());
        verify(transaction, never()).commit();
    }

    @Test
    public void update() {
        when(fileEntityRepository.getById(anyLong())).thenReturn(Optional.of(file3));
        when(fileEntityRepository.update(any(FileEntity.class))).thenReturn(FileTestData.getUpdated());
        Optional<FileEntity> result = fileEntityService.update(FileUtil.createTo(FileTestData.getUpdated().get()));
        assertTrue(result.isPresent());
        assertEquals(file3, result.get());
    }

    @Test
    public void updateWhenFileDoesNotExist() {
        when(fileEntityRepository.getById(anyLong())).thenReturn(Optional.empty());
        Optional<FileEntity> result = fileEntityService.update(new FileTo(FILE_NOT_FOUND_ID, "4.txt", "/4.txt", FileStatus.AVAILABLE));
        assertFalse(result.isPresent());
    }
}