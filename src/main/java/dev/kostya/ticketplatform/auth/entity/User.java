package dev.kostya.ticketplatform.auth.entity;

import dev.kostya.ticketplatform.auth.enums.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends SoftDeletableEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq"
    )
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 80)
    private UserStatus userStatus;

    @Override
    public void markDeleted(UUID operationId) {
        long timestamp = System.currentTimeMillis();
        this.username = "deleted@" + timestamp + "_" + this.username;
        this.email = "deleted." + timestamp + "." + this.email;

        super.markDeleted(operationId);
    }

    @Override
    public UUID restore() {
        if (this.username != null) {
            this.username = this.username.replaceFirst(
                    "^deleted@\\d+_",
                    ""
            );
        }

        if (this.email != null) {
            this.email = this.email.replaceFirst(
                    "^deleted\\.\\d+\\.",
                    ""
            );
        }
        return super.restore();
    }
}
