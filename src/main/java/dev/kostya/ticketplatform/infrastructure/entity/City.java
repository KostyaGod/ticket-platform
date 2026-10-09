package dev.kostya.ticketplatform.infrastructure.entity;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cities")
@Getter @Setter
@NoArgsConstructor
public class City extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_seq")
    @SequenceGenerator(
            name = "city_seq",
            sequenceName = "city_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @Column(nullable = false, length = 64)
    private String timezone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "city")
    private List<Venue> venue;
}
