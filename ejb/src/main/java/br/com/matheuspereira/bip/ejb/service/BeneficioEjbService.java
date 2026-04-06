package br.com.matheuspereira.bip.ejb.service;

import br.com.matheuspereira.bip.ejb.entity.Beneficio;
import br.com.matheuspereira.bip.ejb.exception.BeneficioNotFoundException;
import br.com.matheuspereira.bip.ejb.exception.SaldoInsuficienteException;
import br.com.matheuspereira.bip.ejb.exception.TransferenciaInvalidaException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Serviço EJB para operações de transferência entre benefícios.
 *
 * Usa @Stateless (EJB) combinado com @Transactional (Spring), permitindo
 * rodar tanto em container Jakarta EE quanto em contexto Spring Boot.
 * O locking pessimista (PESSIMISTIC_WRITE) garante atomicidade em cenários
 * de alta concorrência; o campo @Version na entidade age como segunda camada
 * de proteção via optimistic locking.
 */
@Stateless
@Service
@Transactional
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        validateTransferParams(fromId, toId, amount);

        Beneficio from = findWithLock(fromId);
        Beneficio to   = findWithLock(toId);

        Optional.of(from)
                .filter(b -> b.getValor().compareTo(amount) >= 0)
                .orElseThrow(() -> new SaldoInsuficienteException(fromId, from.getValor(), amount));

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));
        // Entidades gerenciadas — JPA sincroniza ao fim da transação automaticamente
    }

    private void validateTransferParams(Long fromId, Long toId, BigDecimal amount) {
        Optional.ofNullable(amount)
                .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
                .orElseThrow(() -> new TransferenciaInvalidaException("O valor da transferência deve ser positivo"));

        Optional.ofNullable(fromId)
                .filter(id -> !id.equals(toId))
                .orElseThrow(() -> new TransferenciaInvalidaException("Origem e destino não podem ser iguais"));
    }

    private Beneficio findWithLock(Long id) {
        return Optional.ofNullable(em.find(Beneficio.class, id, LockModeType.PESSIMISTIC_WRITE))
                .orElseThrow(() -> new BeneficioNotFoundException(id));
    }
}
