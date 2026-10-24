package dev.kostya.ticketplatform.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_seats_hall_row_number",
                        columnNames = {"hall_id", "row_number", "seat_number"}
                )
        }
)
@Getter @Setter
@NoArgsConstructor
public class Seat extends SoftDeletableEntity implements IdentifiableAndNamed {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seat_seq"
    )
    @SequenceGenerator(
            name = "seat_seq",
            sequenceName = "seat_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    public Long getHallId() {
        return hall.getId();
    }

    public String getName() {
        return rowNumber + "-" + seatNumber;
    }
}
