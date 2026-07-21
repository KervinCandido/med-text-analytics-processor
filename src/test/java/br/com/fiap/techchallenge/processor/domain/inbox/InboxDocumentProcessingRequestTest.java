package br.com.fiap.techchallenge.processor.domain.inbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InboxDocumentProcessingRequestTest {

    private InboxDocumentProcessingRequest inbox;

    @BeforeEach
    void setUp() {
        inbox = new InboxDocumentProcessingRequest(
                "inbox-id",
                UUID.randomUUID(),
                UUID.randomUUID(),
                "/documents/exame.png",
                UUID.randomUUID()
        );
    }

    @Test
    void shouldStartAsPending() {
        assertEquals(
                ProcessingStatus.PENDING,
                inbox.getStatus()
        );

        assertEquals(
                (short) 0,
                inbox.getRetryCount()
        );
    }

    @Test
    void shouldMarkSuccessfulProcessingAsProcessed() {
        inbox.processing();
        inbox.processed();

        assertEquals(
                ProcessingStatus.PROCESSED,
                inbox.getStatus()
        );

        assertEquals(
                (short) 0,
                inbox.getRetryCount()
        );

        assertNotNull(inbox.getProcessedAt());
    }

    @Test
    void shouldReachAllRetryFailedAfterRetryLimit() {
        for (
                int attempt = 1;
                attempt <= InboxDocumentProcessingRequest.RETRY_LIMIT;
                attempt++
        ) {
            inbox.processing();
            inbox.failed();

            assertEquals(
                    ProcessingStatus.FAILED,
                    inbox.getStatus()
            );

            assertEquals(
                    (short) attempt,
                    inbox.getRetryCount()
            );
        }

        inbox.processing();
        inbox.failed();

        assertEquals(
                ProcessingStatus.ALL_RETRY_FAILED,
                inbox.getStatus()
        );

        assertEquals(
                (short) (
                        InboxDocumentProcessingRequest.RETRY_LIMIT + 1
                ),
                inbox.getRetryCount()
        );

        assertNotNull(inbox.getProcessedAt());
    }

    @Test
    void shouldFailPermanentlyWithoutAdditionalRetries() {
        inbox.processing();
        inbox.failPermanently();

        assertEquals(
                ProcessingStatus.ALL_RETRY_FAILED,
                inbox.getStatus()
        );

        assertEquals(
                (short) 1,
                inbox.getRetryCount()
        );

        assertNotNull(inbox.getProcessedAt());
    }
}
