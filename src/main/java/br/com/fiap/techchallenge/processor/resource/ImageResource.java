package br.com.fiap.techchallenge.processor.resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJob;
import br.com.fiap.techchallenge.processor.dto.JobStatusResponseDTO;
import jakarta.inject.Inject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/images")
public class ImageResource {

    private static final Logger logger = LoggerFactory.getLogger(ImageResource.class);

    private final ObjectMapper objectMapper;

    @Inject
    public ImageResource(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobStatus(@PathParam("id") String id) {
        logger.info("action=getJobStatus, jobId={}", id);
        ProcessamentoJob job = ProcessamentoJob.findById(id).orElseThrow();
        if (job == null) {
            logger.warn("action=getJobStatusNotFound, jobId={}", id);
            return Response.status(Response.Status.NOT_FOUND).entity("Job not found").build();
        }

        // Reconstruct results to DTO
        List<JsonNode> parsedResultados = null;
        if (job.getResultados() != null) {
            parsedResultados = new ArrayList<>();
            for (String resJson : job.getResultados()) {
                try {
                    parsedResultados.add(objectMapper.readTree(resJson));
                } catch (Exception e) {
                    logger.error("action=jsonNodeReconstructError, jobId={}, reason={}", id, e.getMessage());
                }
            }
        }

        JobStatusResponseDTO responseDto = new JobStatusResponseDTO(
                job.getId(),
                job.getStatus(),
                job.getTotalFiles(),
                job.getProcessedFiles(),
                job.getRetryCount(),
                job.getImagensSucesso(),
                job.getImagensFalha(),
                job.getCreatedAt(),
                job.getCompletedAt(),
                job.getErrorDetail(),
                parsedResultados
        );

        return Response.ok(responseDto).build();
    }

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDocuments(@PathParam("userId") String userId) {
        if (userId == null || userId.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User ID is required").build();
        }
        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid UUID format for User ID").build();
        }

        logger.info("action=getUserDocuments, userId={}", userId);

        List<Object> documents = new ArrayList<>();
        documents.addAll(br.com.fiap.techchallenge.processor.domain.exame.Exame.list("userId", userId));
        documents.addAll(br.com.fiap.techchallenge.processor.domain.laudo.Laudo.list("userId", userId));
        documents.addAll(br.com.fiap.techchallenge.processor.domain.receita.Receita.list("userId", userId));
        documents.addAll(br.com.fiap.techchallenge.processor.domain.relatorio.Relatorio.list("userId", userId));
        documents.addAll(br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento.list("userId", userId));

        // Sort by dataDocumento descending (most recent first)
        documents.sort((a, b) -> {
            LocalDateTime dateA = getDataDocumento(a);
            LocalDateTime dateB = getDataDocumento(b);
            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;
            if (dateB == null) return -1;
            return dateB.compareTo(dateA); // Descending
        });

        return Response.ok(documents).build();
    }

    private LocalDateTime getDataDocumento(Object obj) {
        if (obj instanceof br.com.fiap.techchallenge.processor.domain.exame.Exame e) return e.getDocumentDate();
        if (obj instanceof br.com.fiap.techchallenge.processor.domain.laudo.Laudo l) return l.getDocumentDate();
        if (obj instanceof br.com.fiap.techchallenge.processor.domain.receita.Receita r) return r.getDocumentDate();
        if (obj instanceof br.com.fiap.techchallenge.processor.domain.relatorio.Relatorio rep) return rep.getDocumentDate();
        if (obj instanceof br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento enc) return enc.getDocumentDate();
        return null;
    }

}
