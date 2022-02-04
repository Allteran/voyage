package allteran.voyage.repo;

import allteran.voyage.domain.PayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayTypeRepo extends JpaRepository<PayType, Long> {
}
