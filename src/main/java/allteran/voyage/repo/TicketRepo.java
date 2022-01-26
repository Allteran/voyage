package allteran.voyage.repo;

import allteran.voyage.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByCustomer(String customer);

    @Query("select t from Ticket t order by t.issueDate")
    List<Ticket> findAllSorted();
}
