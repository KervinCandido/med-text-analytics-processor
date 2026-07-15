package br.com.fiap.techchallenge.processor.persistence.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

@ApplicationScoped
public class ObjectIdMapper {

    public ObjectId map(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return new ObjectId(id);
    }

    public String map(ObjectId objectId) {
        if (objectId == null) {
            return null;
        }
        return objectId.toHexString();
    }

//    @Named("stringListToObjectIdList")
//    public List<ObjectId> map(List<String> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return ids.stream().map(this::map).toList();
//    }
//
//    @Named("objectIdListToStringList")
//    public List<String> mapObjectIdToString(List<ObjectId> objectIds) {
//        if (objectIds == null || objectIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return objectIds.stream().map(this::map).toList();
//    }
}
