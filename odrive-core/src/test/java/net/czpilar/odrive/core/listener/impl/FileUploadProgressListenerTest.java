package net.czpilar.odrive.core.listener.impl;

import net.czpilar.odrive.core.listener.IFileUploadProgressListener;
import org.junit.jupiter.api.Test;

public class FileUploadProgressListenerTest {

    private final FileUploadProgressListener listener = new FileUploadProgressListener("test-file-name", 1000);

    @Test
    public void testProgressChangedInitiation() {
        listener.progressChanged(IFileUploadProgressListener.State.INITIATION, 0);
    }

    @Test
    public void testProgressChangedInProgress() {
        listener.progressChanged(IFileUploadProgressListener.State.IN_PROGRESS, 230);
    }

    @Test
    public void testProgressChangedInProgressWhereZeroLength() {
        FileUploadProgressListener zeroLengthListener = new FileUploadProgressListener("test-file-name", 0);
        zeroLengthListener.progressChanged(IFileUploadProgressListener.State.IN_PROGRESS, 0);
    }

    @Test
    public void testProgressChangedComplete() {
        listener.progressChanged(IFileUploadProgressListener.State.COMPLETE, 1000);
    }
}
