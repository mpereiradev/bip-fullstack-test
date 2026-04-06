package br.com.matheuspereira.bip.api.controller;

import br.com.matheuspereira.bip.api.dto.BeneficioRequest;
import br.com.matheuspereira.bip.api.dto.TransferenciaRequest;
import br.com.matheuspereira.bip.api.exception.GlobalExceptionHandler;
import br.com.matheuspereira.bip.api.service.BeneficioService;
import br.com.matheuspereira.bip.ejb.entity.Beneficio;
import br.com.matheuspereira.bip.ejb.exception.BeneficioNotFoundException;
import br.com.matheuspereira.bip.ejb.exception.SaldoInsuficienteException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeneficioControllerTest {

    @Mock
    BeneficioService service;

    @InjectMocks
    BeneficioController controller;

    MockMvc mvc;
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        mapper = new ObjectMapper();
    }

    @Test
    void listar_deveRetornarListaDeAtivos() throws Exception {
        Beneficio b = beneficio(1L, "Beneficio A", new BigDecimal("1000.00"));
        when(service.listarAtivos()).thenReturn(List.of(b));

        mvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Beneficio A"))
                .andExpect(jsonPath("$[0].valor").value(1000.00));
    }

    @Test
    void buscar_deveRetornar404QuandoNaoEncontrado() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new BeneficioNotFoundException(99L));

        mvc.perform(get("/api/v1/beneficios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("99")));
    }

    @Test
    void criar_deveRetornar201ComDadosValidos() throws Exception {
        BeneficioRequest req = new BeneficioRequest("Novo", "Desc", new BigDecimal("200.00"));
        Beneficio criado = beneficio(3L, "Novo", new BigDecimal("200.00"));
        when(service.criar(any())).thenReturn(criado);

        mvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void criar_deveRetornar400QuandoNomeBranco() throws Exception {
        BeneficioRequest req = new BeneficioRequest("", "Desc", new BigDecimal("200.00"));

        mvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void desativar_deveRetornar204() throws Exception {
        doNothing().when(service).desativar(1L);

        mvc.perform(delete("/api/v1/beneficios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void transferir_deveRetornar204ComDadosValidos() throws Exception {
        TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("100.00"));
        doNothing().when(service).transferir(1L, 2L, new BigDecimal("100.00"));

        mvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void transferir_deveRetornar422QuandoSaldoInsuficiente() throws Exception {
        TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("9999.00"));
        doThrow(new SaldoInsuficienteException(1L, new BigDecimal("100.00"), new BigDecimal("9999.00")))
                .when(service).transferir(eq(1L), eq(2L), any());

        mvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    private Beneficio beneficio(Long id, String nome, BigDecimal valor) {
        Beneficio b = new Beneficio();
        b.setId(id);
        b.setNome(nome);
        b.setValor(valor);
        b.setAtivo(true);
        return b;
    }
}
