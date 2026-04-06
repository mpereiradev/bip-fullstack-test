package br.com.matheuspereira.bip.ejb.exception;

public class BeneficioNotFoundException extends RuntimeException {

    public BeneficioNotFoundException(Long id) {
        super("Beneficio não encontrado com id: " + id);
    }
}
