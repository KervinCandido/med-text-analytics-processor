package br.com.fiap.techchallenge.processor.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJob;
import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJobStatus;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ErrorRetryScheduler {

    private static final ZoneId SAO_PAULO_ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final Logger logger = LoggerFactory.getLogger(ErrorRetryScheduler.class);

    @ConfigProperty(name = "app.retry.max-attempts", defaultValue = "5")
    int maxRetries;

    @Inject
    JobStatusUpdater jobStatusUpdater;

    @Scheduled(every = "${app.retry.scheduler.interval:5m}")
    public void retryFailedJobs() {
        logger.info("action=retryFailedJobsStart, maxRetriesConfig={}", maxRetries);
        try {
            // Find all jobs with status ERRO_NO_PROCESSAMENTO that haven't exceeded the retry threshold
            List<ProcessamentoJob> failedJobs = ProcessamentoJob.list(
                    "status = ?1 and retryCount < ?2", 
                    ProcessamentoJobStatus.ERRO_NO_PROCESSAMENTO, 
                    maxRetries
            );

            logger.info("action=retryFailedJobsEligibleCount, count={}", failedJobs.size());

            for (ProcessamentoJob job : failedJobs) {

                logger.info("action=retryJobTriggered, jobId={}, failedFilesCount={}, currentRetry={}", 
                        job.getId(), job.getImagensFalha().size(), job.getRetryCount());

                List<String> failedImagesToRetry = new ArrayList<>(job.getImagensFalha());

                // Re-queue each failed image by creating a new OutboxEvent
                for (String failedImagePath : failedImagesToRetry) {
                    OutboxEvent outboxEvent = new OutboxEvent();
                    outboxEvent.jobId = job.getId();
                    outboxEvent.filePath = failedImagePath;
                    outboxEvent.userId = job.getUserId();
                    outboxEvent.status = "PENDING";
                    outboxEvent.createdAt = LocalDateTime.now(SAO_PAULO_ZONE_ID);
                    outboxEvent.persist();
                }

                // Adjust job metadata
                job.setProcessedFiles(job.getTotalFiles() - failedImagesToRetry.size());
                job.limpaFalhas();
                job.retryIncrement(); // Increment retry count
                job.setStatus(ProcessamentoJobStatus.PROCESSANDO);
                jobStatusUpdater.updateAndQueueStatus(job);

                logger.info("action=retryJobQueued, jobId={}, newRetryCount={}, resetProgress={}/{}", 
                        job.getId(), job.getRetryCount(), job.getProcessedFiles(), job.getTotalFiles());
            }
        } catch (Exception e) {
            logger.error("action=retryFailedJobsError, reason={}", e.getMessage(), e);
        }
    }
}
