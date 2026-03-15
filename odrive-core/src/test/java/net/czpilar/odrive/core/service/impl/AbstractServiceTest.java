package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.credential.IODriveCredential;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractServiceTest {

    private AbstractService service;

    @Mock
    private IODriveCredential oDriveCredential;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private OneDriveClient oneDriveClient;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        service = new AbstractService() {
        };
        service.setApplicationContext(applicationContext);

        when(applicationContext.getBean(OneDriveClient.class)).thenReturn(oneDriveClient);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testSetAndGetODriveCredential() {
        assertNull(service.getODriveCredential());

        service.setODriveCredential(oDriveCredential);

        assertEquals(oDriveCredential, service.getODriveCredential());
    }

    @Test
    void testGetOneDriveClient() {
        OneDriveClient result = service.getOneDriveClient();

        assertNotNull(result);
        assertEquals(oneDriveClient, result);

        verify(applicationContext).getBean(OneDriveClient.class);

        verifyNoMoreInteractions(applicationContext);
    }
}
