package allteran.voyage.service;

import allteran.voyage.domain.PointOfSales;
import allteran.voyage.repo.POSRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class POSService {
    private final POSRepo posRepo;

    @Autowired
    public POSService(POSRepo posRepo) {
        this.posRepo = posRepo;
    }

    public List<PointOfSales> findAll() {
        return posRepo.findAll();
    }
}
