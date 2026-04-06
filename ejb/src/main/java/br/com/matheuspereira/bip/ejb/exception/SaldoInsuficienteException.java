package br.com.matheuspereira.bip.ejb.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long id, BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super(String.format(
            "Beneficio %d possui saldo %.2f, insuficiente para transferência de %.2f",
            id, saldoAtual, valorSolicitado
        ));
    }
}
