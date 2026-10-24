package dev.kostya.ticketplatform.auth.mapper;

import dev.kostya.ticketplatform.auth.entity.SoftDeletableEntity;
import dev.kostya.ticketplatform.auth.dto.base.EntityMetadata;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface CentralMapperConfig {
    default <T extends SoftDeletableEntity> EntityMetadata<T> mapMetadata(T entity) {
        return entity != null ? new EntityMetadata<>(entity) : null;
    }
}
