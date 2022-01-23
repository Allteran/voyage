package allteran.voyage.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class Ticket {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate issueDate;
    private LocalDate departureDate;

    private String reservationNumber;
    private String ticketNumber;

    private String customer;
    private String flightRoute;

    private int price;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TicketType type;

    private String passport;

    private LocalDate dateOfBirth;
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TicketStatus status;

    private String comment;
}
