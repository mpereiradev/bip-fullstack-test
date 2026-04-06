package br.com.matheuspereira.bip.api.repository;

import br.com.matheuspereira.bip.ejb.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    List<Beneficio> findAllByAtivoTrue();
}
