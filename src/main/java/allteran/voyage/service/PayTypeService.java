package allteran.voyage.service;

import allteran.voyage.domain.PayType;
import allteran.voyage.repo.PayTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayTypeService {
    private final PayTypeRepo payTypeRepo;

    @Autowired
    public PayTypeService(PayTypeRepo payTypeRepo) {
        this.payTypeRepo = payTypeRepo;
    }

    public List<PayType> findAll() {
        return payTypeRepo.findAll();
    }
}
