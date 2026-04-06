package br.com.matheuspereira.bip.ejb.exception;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long id, BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super(String.format(
            "Beneficio %d possui saldo %s, insuficiente para transferência de %s",
            id,
            format(saldoAtual),
            format(valorSolicitado)
        ));
    }

    private static String format(BigDecimal value) {
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        return fmt.format(value);
    }
}
