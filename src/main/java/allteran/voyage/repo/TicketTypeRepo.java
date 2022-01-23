package allteran.voyage.repo;

import allteran.voyage.domain.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepo extends JpaRepository<TicketType, Long> {
}
