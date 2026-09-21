package gradle.example.com.cinemabooking.models;

public class Seat {

    private Long id;
    private Long hallId;
    private Integer rowNumber;
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
