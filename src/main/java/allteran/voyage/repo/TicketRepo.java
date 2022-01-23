package allteran.voyage.repo;

import allteran.voyage.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByCustomer(String customer);
}
