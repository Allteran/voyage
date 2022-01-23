package allteran.voyage.repo;

import allteran.voyage.domain.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketStatusRepo extends JpaRepository<TicketStatus, Long> {
}
