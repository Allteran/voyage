package allteran.voyage.repo;

import allteran.voyage.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByCustomer(String customer);

    @Query("select t from Ticket t order by t.issueDate")
    List<Ticket> findAllSortedByIssueDate();

    @Query("from Ticket t where t.issueDate =:date order by t.type.id")
    List<Ticket> findByIssueDate(@Param("date") LocalDate date);
}
