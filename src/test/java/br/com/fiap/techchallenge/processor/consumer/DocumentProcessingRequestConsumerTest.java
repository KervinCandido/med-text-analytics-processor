package br.com.fiap.techchallenge.processor.consumer;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import br.com.fiap.techchallenge.processor.service.CreateInboxEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DocumentProcessingRequestConsumerTest {

    private static final UUID EVENT_ID =
            UUID.fromString(
                    "a55c53fa-f3c4-4552-a98f-7d88d16f40e2"
            );

    private static final UUID DOCUMENT_ID =
            UUID.fromString(
                    "60ebc948-f701-462f-bffd-9167eb77b193"
            );

    private static final UUID PATIENT_ID =
            UUID.fromString(
                    "240e349a-e621-44d3-b569-870bf5825939"
            );

    private static final String FILE_URL =
            "https://patient-document-service:8443/documents/"
                    + DOCUMENT_ID
                    + "/file";

    private static final String CONTENT_TYPE = "image/png";

    private static final Instant OCCURRED_AT =
            Instant.parse(
                    "2026-07-21T13:30:15.123Z"
            );

    @Mock
    private CreateInboxEventService createInboxEventService;

    private DocumentProcessingRequestConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer =
                new DocumentProcessingRequestConsumer(
                        createInboxEventService
                );
    }

    @Test
    void shouldPersistVersionOneMessage() {
        consumer.documentProcessingRequest(
                validMessage()
        );

        ArgumentCaptor<InboxDocumentProcessingRequest>
                inboxCaptor =
                ArgumentCaptor.forClass(
                        InboxDocumentProcessingRequest.class
                );

        verify(createInboxEventService)
                .create(inboxCaptor.capture());

        InboxDocumentProcessingRequest inbox =
                inboxCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        EVENT_ID,
                        inbox.getEventId()
                ),
                () -> assertEquals(
                        DOCUMENT_ID,
                        inbox.getDocumentId()
                ),
                () -> assertEquals(
                        PATIENT_ID,
                        inbox.getPatientId()
                ),
                () -> assertEquals(
                        FILE_URL,
                        inbox.getFilePath()
                ),
                () -> assertEquals(
                        CONTENT_TYPE,
                        inbox.getContentType()
                )
        );
    }

    @Test
    void shouldRejectInvalidMessageWithoutPersistence() {
        DocumentProcessingRequestedDTO invalidMessage =
                new DocumentProcessingRequestedDTO(
                        2,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> consumer
                                .documentProcessingRequest(
                                        invalidMessage
                                )
                );

        assertEquals(
                "Unsupported schemaVersion: 2",
                exception.getMessage()
        );

        verifyNoInteractions(createInboxEventService);
    }

    @Test
    void shouldPropagateInboxPersistenceFailure() {
        RuntimeException persistenceFailure =
                new RuntimeException(
                        "Inbox persistence failed"
                );

        doThrow(persistenceFailure)
                .when(createInboxEventService)
                .create(
                        any(
                                InboxDocumentProcessingRequest.class
                        )
                );

        RuntimeException propagatedException =
                assertThrows(
                        RuntimeException.class,
                        () -> consumer
                                .documentProcessingRequest(
                                        validMessage()
                                )
                );

        assertSame(
                persistenceFailure,
                propagatedException
        );
    }

    private static DocumentProcessingRequestedDTO validMessage() {
        return new DocumentProcessingRequestedDTO(
                1,
                "DOCUMENT_PROCESSING_REQUESTED",
                OCCURRED_AT,
                EVENT_ID,
                DOCUMENT_ID,
                PATIENT_ID,
                FILE_URL,
                CONTENT_TYPE
        );
    }
}
