package dev.kostya.ticketplatform.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Базовый класс для сущностей, поддерживающих механизм мягкого логического удаления (Soft Delete).
 * <p>
 * Предотвращает физическое удаление записей из базы данных, сохраняя историю и ссылочную целостность:
 * <ul>
 *   <li>{@code deletedAt} — временная метка (UTC) момента удаления. Если {@code null} — запись активна.</li>
 *   <li>{@code deletionOperationId} — уникальный UUID транзакции/операции удаления. Используется для
 *       координации каскадных распределенных операций удаления и последующего точечного восстановления
 *       связанных сущностей (например, при восстановлении зала восстанавливать только те места, которые
 *       были удалены в рамках именно этой операции).</li>
 * </ul>
 */
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public abstract class SoftDeletableEntity extends BaseAuditEntity {

    @Column(name = "deleted_at")
    protected Instant deletedAt;

    @Column(name = "deletion_operation_id")
    protected UUID deletionOperationId;

    /**
     * Проверяет, помечена ли сущность как удаленная.
     *
     * @return {@code true}, если сущность удалена
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Помечает сущность как удаленную в рамках указанной распределенной операции.
     *
     * @param operationId идентификатор операции каскадного удаления
     */
    public void markDeleted(UUID operationId) {
        this.deletedAt = Instant.now();
        this.deletionOperationId = operationId;
    }

    /**
     * Помечает сущность как удаленную, генерируя новый уникальный идентификатор операции удаления.
     *
     * @return сгенерированный UUID операции
     */
    public UUID markDeleted() {
        UUID operationId = UUID.randomUUID();
        markDeleted(operationId);
        return operationId;
    }

    /**
     * Восстанавливает сущность из мягкого удаления.
     *
     * @return сохраненный ранее идентификатор операции удаления для возможного каскадного отката
     */
    public UUID restore() {
        UUID operationId = this.deletionOperationId;
        this.deletedAt = null;
        this.deletionOperationId = null;
        return operationId;
    }
}
