package com.example.algamoney.api.repository.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.algamoney.api.model.Pessoa;

public interface PessoaRepositoryQuery {

	Page<Pessoa> listarPessoaOrderByNome(String nome, Pageable pageable);
	
}
