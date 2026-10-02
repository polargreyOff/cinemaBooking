package gradle.example.com.cinemabooking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("seat")
public class Seat {

    @Id
    private Long id;
    @Column("hall_id")
    private Long hallId;
    @Column("row_number")
    private Integer rowNumber;
    @Column("seat_number")
    private Integer seatNumber;

    public Seat() {}

    public Seat(
            Long id,
            Long hallId,
            Integer rowNumber,
            Integer seatNumber
    ) {
        this.id = id;
        this.hallId = hallId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
    }

    public Long getId() {
        return id;
    }

    public Long getHallId() {
        return hallId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }
}
