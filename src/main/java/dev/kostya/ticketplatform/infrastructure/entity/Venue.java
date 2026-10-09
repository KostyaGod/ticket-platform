package dev.kostya.ticketplatform.infrastructure.entity;

import dev.kostya.ticketplatform.infrastructure.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="venues")
@Getter @Setter
@NoArgsConstructor
public class Venue extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "venue_seq")
    @SequenceGenerator(
            name = "venue_seq",
            sequenceName = "venue_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "venue")
    private List<Hall> halls = new ArrayList<>();
}
