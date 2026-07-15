package br.com.fiap.techchallenge.processor.persistence;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.LaudoEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LaudoRepository implements PanacheMongoRepository<LaudoEntity> {
    public List<LaudoEntity> list() {
        return listAll();
    }
}
