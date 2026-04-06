package br.com.matheuspereira.bip.api.service;

import br.com.matheuspereira.bip.api.dto.BeneficioRequest;
import br.com.matheuspereira.bip.api.repository.BeneficioRepository;
import br.com.matheuspereira.bip.ejb.entity.Beneficio;
import br.com.matheuspereira.bip.ejb.exception.BeneficioNotFoundException;
import br.com.matheuspereira.bip.ejb.service.BeneficioEjbService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BeneficioService {

    private final BeneficioRepository repository;
    private final BeneficioEjbService ejbService;

    public BeneficioService(BeneficioRepository repository, BeneficioEjbService ejbService) {
        this.repository = repository;
        this.ejbService = ejbService;
    }

    public List<Beneficio> listarAtivos() {
        return repository.findAllByAtivoTrue();
    }

    public Beneficio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
    }

    @Transactional
    public Beneficio criar(BeneficioRequest request) {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(request.nome());
        beneficio.setDescricao(request.descricao());
        beneficio.setValor(request.valor());
        return repository.save(beneficio);
    }

    @Transactional
    public Beneficio atualizar(Long id, BeneficioRequest request) {
        Beneficio beneficio = buscarPorId(id);
        beneficio.setNome(request.nome());
        beneficio.setDescricao(request.descricao());
        beneficio.setValor(request.valor());
        return repository.save(beneficio);
    }

    @Transactional
    public void desativar(Long id) {
        Beneficio beneficio = buscarPorId(id);
        beneficio.setAtivo(false);
        repository.save(beneficio);
    }

    @Transactional
    public void transferir(Long fromId, Long toId, BigDecimal valor) {
        ejbService.transfer(fromId, toId, valor);
    }
}
