package com.example.algamoney.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.example.algamoney.api.AlgamoneyApiApplication;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.storage.S3;

public class LancamentoAnexoListener {

	// Obs: quem vai construir (injetar) esta classe é o hibernate e não o spring, por isso não podemos	utilizar
	// a injeção de dependências
	
	// o @PostLoad indica que o evento vai ser efetuar após o carregamento;
	// o nome do método pode ser qualquer nome;
	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if (StringUtils.hasText(lancamento.getAnexo())) {
			S3 s3 = AlgamoneyApiApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}
}
