package allteran.voyage.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class Ticket {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate issueDate;//*
    private LocalDate departureDate;//*

    private String reservationNumber;//*
    private String ticketNumber;//*

    private String customer; //*
    private String flightRoute;//*

    private double tariffPrice;//*
    private double taxYQPrice;//*
    private double taxRUYRPrice;//*

    private double totalPrice;//*

    @ManyToOne
    @JoinColumn(name = "pay_type_id")
    private PayType payType; //*

    @ManyToOne
    @JoinColumn(name = "pos_id")
    private PointOfSales pos;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TicketType type;//*

    private String passport;//*

    private LocalDate dateOfBirth;//*
    private String customerPhone; //*

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String comment;//*
}
