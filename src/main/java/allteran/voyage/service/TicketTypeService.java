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

    public TicketType save(TicketType type) {
        return typeRepo.save(type);
    }

    public void delete(TicketType type) {
        typeRepo.delete(type);
    }

    public TicketType findById(Long id, TicketType newType) {
        return typeRepo.findById(id).orElse(newType);
    }
}
