package um.tesoreria.rest.service;

import um.tesoreria.rest.exception.BancariaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.rest.exception.BancariaException;
import um.tesoreria.rest.kotlin.model.Bancaria;
import um.tesoreria.rest.kotlin.repository.IBancariaRepository;

import java.util.List;

@Service
public class BancariaService {

    @Autowired
    private IBancariaRepository repository;

    public List<Bancaria> findAll() {
        return repository.findAll();
    }

    public Bancaria findByBancariaId(Long bancariaId) {
        return repository.findByBancariaId(bancariaId).orElseThrow(() -> new BancariaException(bancariaId));
    }
}
