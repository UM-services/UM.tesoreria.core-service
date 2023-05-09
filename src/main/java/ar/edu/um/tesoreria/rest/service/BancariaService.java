package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.exception.BancariaException;
import ar.edu.um.tesoreria.rest.kotlin.model.Bancaria;
import ar.edu.um.tesoreria.rest.kotlin.repository.IBancariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
