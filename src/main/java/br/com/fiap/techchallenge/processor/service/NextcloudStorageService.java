package br.com.fiap.techchallenge.processor.service;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Slf4j
@ApplicationScoped
public class NextcloudStorageService {

    private final String username;

    private final String password;

    @Inject
    public NextcloudStorageService(
            @ConfigProperty(name = "nextcloud.username") String username,
            @ConfigProperty(name = "nextcloud.app-password") String password) {
        this.username = username;
        this.password = password;
    }

    public byte[] load(String storagePath) {
        try {
            Sardine sardine = SardineFactory.begin(username, password);
            log.info("verificando se existe arquivo: {}", storagePath);
            if (!sardine.exists(storagePath)) {
                throw new RuntimeException("Arquivo não encontrado.");
            }
            try (var is = sardine.get(storagePath)) {
                return is.readAllBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível ler o arquivo armazenado.", e);
        }
    }

    public void delete(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            throw new RuntimeException(
                    "O caminho do arquivo armazenado não foi informado."
            );
        }

        try {
            Sardine sardine =
                    SardineFactory.begin(username, password);

            if (sardine.exists(storagePath)) {
                sardine.delete(storagePath);
            }
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Não foi possível excluir o arquivo armazenado "
                            + "no Nextcloud.",
                    exception
            );
        }
    }
}
