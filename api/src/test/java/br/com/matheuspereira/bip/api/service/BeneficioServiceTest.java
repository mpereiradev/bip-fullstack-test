package br.com.matheuspereira.bip.api.service;

import br.com.matheuspereira.bip.api.dto.BeneficioRequest;
import br.com.matheuspereira.bip.api.repository.BeneficioRepository;
import br.com.matheuspereira.bip.ejb.entity.Beneficio;
import br.com.matheuspereira.bip.ejb.exception.BeneficioNotFoundException;
import br.com.matheuspereira.bip.ejb.service.BeneficioEjbService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock BeneficioRepository repository;
    @Mock BeneficioEjbService ejbService;
    @InjectMocks BeneficioService service;

    @Test
    void listarAtivos_deveRetornarApenasAtivos() {
        Beneficio b = new Beneficio();
        b.setAtivo(true);
        when(repository.findAllByAtivoTrue()).thenReturn(List.of(b));

        assertThat(service.listarAtivos()).hasSize(1);
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(BeneficioNotFoundException.class);
    }

    @Test
    void criar_deveSalvarComDadosCorretos() {
        BeneficioRequest req = new BeneficioRequest("Teste", "Desc", new BigDecimal("100.00"));
        Beneficio saved = new Beneficio();
        saved.setId(1L);
        saved.setNome("Teste");
        when(repository.save(any())).thenReturn(saved);

        Beneficio result = service.criar(req);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Teste");
    }

    @Test
    void desativar_deveSetarAtivoFalse() {
        Beneficio b = new Beneficio();
        b.setId(1L);
        b.setAtivo(true);
        when(repository.findById(1L)).thenReturn(Optional.of(b));
        when(repository.save(any())).thenReturn(b);

        service.desativar(1L);

        assertThat(b.getAtivo()).isFalse();
        verify(repository).save(b);
    }

    @Test
    void transferir_deveDelegarAoEjbService() {
        service.transferir(1L, 2L, new BigDecimal("100.00"));

        verify(ejbService).transfer(1L, 2L, new BigDecimal("100.00"));
    }
}
