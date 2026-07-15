package br.com.fiap.techchallenge.processor.persistence;

import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class DocumentoRepository implements PanacheMongoRepository<DocumentoEntity> {

    public List<DocumentoEntity> buscaDocumentosPorIds(List<ObjectId> documentsIds) {
        return list("_id in ?1", documentsIds);
    }
}
