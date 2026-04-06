package br.com.matheuspereira.bip.ejb.service;

import br.com.matheuspereira.bip.ejb.entity.Beneficio;
import br.com.matheuspereira.bip.ejb.exception.BeneficioNotFoundException;
import br.com.matheuspereira.bip.ejb.exception.SaldoInsuficienteException;
import br.com.matheuspereira.bip.ejb.exception.TransferenciaInvalidaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private BeneficioEjbService service;

    private Beneficio from;
    private Beneficio to;

    @BeforeEach
    void setUp() {
        from = new Beneficio();
        from.setId(1L);
        from.setNome("Beneficio A");
        from.setValor(new BigDecimal("1000.00"));

        to = new Beneficio();
        to.setId(2L);
        to.setNome("Beneficio B");
        to.setValor(new BigDecimal("500.00"));
    }

    @Test
    void transfer_deveDebitarOrigemECreditarDestino() {
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(from);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(to);

        service.transfer(1L, 2L, new BigDecimal("300.00"));

        assertThat(from.getValor()).isEqualByComparingTo("700.00");
        assertThat(to.getValor()).isEqualByComparingTo("800.00");
    }

    @Test
    void transfer_deveLancarExcecaoQuandoSaldoInsuficiente() {
        when(em.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(from);
        when(em.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(to);

        assertThatThrownBy(() -> service.transfer(1L, 2L, new BigDecimal("1500.00")))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining("1.000,00")
                .hasMessageContaining("1.500,00");
    }

    @Test
    void transfer_deveLancarExcecaoQuandoValorNegativo() {
        assertThatThrownBy(() -> service.transfer(1L, 2L, new BigDecimal("-1.00")))
                .isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining("positivo");
    }

    @Test
    void transfer_deveLancarExcecaoQuandoValorZero() {
        assertThatThrownBy(() -> service.transfer(1L, 2L, BigDecimal.ZERO))
                .isInstanceOf(TransferenciaInvalidaException.class);
    }

    @Test
    void transfer_deveLancarExcecaoQuandoOrigemIgualDestino() {
        assertThatThrownBy(() -> service.transfer(1L, 1L, new BigDecimal("100.00")))
                .isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining("iguais");
    }

    @Test
    void transfer_deveLancarExcecaoQuandoBeneficioNaoEncontrado() {
        when(em.find(Beneficio.class, 99L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        assertThatThrownBy(() -> service.transfer(99L, 2L, new BigDecimal("100.00")))
                .isInstanceOf(BeneficioNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void transfer_deveLancarExcecaoQuandoValorNulo() {
        assertThatThrownBy(() -> service.transfer(1L, 2L, null))
                .isInstanceOf(TransferenciaInvalidaException.class);
    }
}
