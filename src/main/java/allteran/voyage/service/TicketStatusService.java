package allteran.voyage.service;

import allteran.voyage.domain.TicketStatus;
import allteran.voyage.repo.TicketStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketStatusService {
    private final TicketStatusRepo statusRepo;

    @Autowired
    public TicketStatusService(TicketStatusRepo statusRepo) {
        this.statusRepo = statusRepo;
    }

    public List<TicketStatus> findAll() {
        return statusRepo.findAll();
    }
}
