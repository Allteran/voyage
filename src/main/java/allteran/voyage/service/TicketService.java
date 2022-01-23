package allteran.voyage.service;

import allteran.voyage.domain.Ticket;
import allteran.voyage.domain.TicketStatus;
import allteran.voyage.domain.TicketType;
import allteran.voyage.repo.TicketRepo;
import allteran.voyage.repo.TicketStatusRepo;
import allteran.voyage.repo.TicketTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepo ticketRepo;
    private final TicketStatusRepo ticketStatusRepo;
    private final TicketTypeRepo ticketTypeRepo;

    @Autowired
    public TicketService(TicketRepo ticketRepo, TicketStatusRepo ticketStatusRepo, TicketTypeRepo ticketTypeRepo) {
        this.ticketRepo = ticketRepo;
        this.ticketStatusRepo = ticketStatusRepo;
        this.ticketTypeRepo = ticketTypeRepo;
    }

    public List<Ticket> findAll() {
        return ticketRepo.findAll();
    }

    public List<Ticket> findByCustomer(String customer) {
        return ticketRepo.findAllByCustomer(customer);
    }

    public Ticket findById(Long id, Ticket newTicket) {
        return ticketRepo.findById(id).orElse(newTicket);
    }

    public void delete(Ticket ticket) {
        ticketRepo.delete(ticket);
    }

    public Ticket save(Ticket ticket) {
        return ticketRepo.save(ticket);
    }


//    @PostConstruct
//    public void init() {
//        TicketType type = new TicketType();
//        type.setName("BSP-M");
//        ticketTypeRepo.save(type);
//
//        TicketStatus status = new TicketStatus();
//        status.setName("NO_SHOW");
//        ticketStatusRepo.save(status);
//
//        List<Ticket> tickets = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Ticket ticket = new Ticket();
//
//            ticket.setIssueDate(LocalDate.now());
//            ticket.setDepartureDate(LocalDate.now());
//            ticket.setReservationNumber("123" + i);
//            ticket.setCustomer("Ваадинов Олень №" + i);
//            ticket.setFlightRoute("Москва - Казань - Иерусалим");
//            ticket.setPrice(500 + i);
//            ticket.setType(type);
//            ticket.setPassport("4715567123");
//            ticket.setDateOfBirth(LocalDate.now());
//            ticket.setCustomerPhone("79123456765");
//            ticket.setStatus(status);
//            ticket.setComment("no comment here");
//
//            tickets.add(ticket);
//        }
//
//        ticketRepo.saveAll(tickets);
//    }
}
