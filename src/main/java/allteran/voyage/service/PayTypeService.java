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

    public void delete(PayType payType) {
        payTypeRepo.delete(payType);
    }

    public PayType save(PayType payType) {
        return payTypeRepo.save(payType);
    }

    public PayType findById(Long id, PayType p) {
        return payTypeRepo.findById(id).orElse(p);
    }
}
