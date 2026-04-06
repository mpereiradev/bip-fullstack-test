package br.com.matheuspereira.bip.api.controller;

import br.com.matheuspereira.bip.api.dto.BeneficioRequest;
import br.com.matheuspereira.bip.api.dto.BeneficioResponse;
import br.com.matheuspereira.bip.api.dto.TransferenciaRequest;
import br.com.matheuspereira.bip.api.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "CRUD de benefícios e transferência de valores")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar benefícios ativos")
    public List<BeneficioResponse> listar() {
        return service.listarAtivos().stream()
                .map(BeneficioResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID",
               responses = {@ApiResponse(responseCode = "404", description = "Não encontrado")})
    public BeneficioResponse buscar(@PathVariable Long id) {
        return BeneficioResponse.from(service.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo benefício")
    public BeneficioResponse criar(@RequestBody @Valid BeneficioRequest request) {
        return BeneficioResponse.from(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício existente",
               responses = {@ApiResponse(responseCode = "404", description = "Não encontrado")})
    public BeneficioResponse atualizar(@PathVariable Long id, @RequestBody @Valid BeneficioRequest request) {
        return BeneficioResponse.from(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar benefício (soft delete)",
               responses = {@ApiResponse(responseCode = "404", description = "Não encontrado")})
    public void desativar(@PathVariable Long id) {
        service.desativar(id);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Transferir valor entre dois benefícios",
               responses = {
                   @ApiResponse(responseCode = "422", description = "Saldo insuficiente"),
                   @ApiResponse(responseCode = "400", description = "Dados inválidos")
               })
    public void transferir(@RequestBody @Valid TransferenciaRequest request) {
        service.transferir(request.fromId(), request.toId(), request.valor());
    }
}
