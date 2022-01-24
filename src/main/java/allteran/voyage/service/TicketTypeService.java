package allteran.voyage.service;

import allteran.voyage.domain.TicketType;
import allteran.voyage.repo.TicketTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {
    private final TicketTypeRepo typeRepo;

    @Autowired
    public TicketTypeService(TicketTypeRepo typeRepo) {
        this.typeRepo = typeRepo;
    }

    public List<TicketType> findAll() {
        return typeRepo.findAll();
    }
}
