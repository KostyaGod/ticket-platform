package dev.kostya.ticketplatform.event.mapper;

import dev.kostya.ticketplatform.event.dto.base.EntityMetadata;
import dev.kostya.ticketplatform.event.dto.base.EntityReference;
import dev.kostya.ticketplatform.event.entity.IdentifiableAndNamed;
import dev.kostya.ticketplatform.event.entity.SoftDeletableEntity;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface CentralMapperConfig {
    default <T extends SoftDeletableEntity> EntityMetadata<T> mapMetadata(T entity) {
        return entity != null ? new EntityMetadata<>(entity) : null;
    }

    default <T extends IdentifiableAndNamed> EntityReference<T> mapReference(T entity) {
        return entity != null ? new EntityReference<>(entity) : null;
    }

    default <T extends IdentifiableAndNamed> List<EntityReference<T>> mapReferenceList(
            List<T> entities
    ) {
        if (entities == null) return null;

        return entities.stream()
                .map( EntityReference::new )
                .toList();
    }
}
