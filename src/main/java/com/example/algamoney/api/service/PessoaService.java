package com.example.algamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa salvar(Pessoa pessoa) {
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		return pessoaRepository.save(pessoa);
	}
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
	
		// alterações para que o parâmetro orphanRemoval = true em Pessoa.java funcione
		// Se não fizer isso, dará o seguinte erro ao chamar a API ( put http://localhost:8080/pessoas/22 ):
//		{
//		    "timestamp": "2019-05-20",
//		    "status": 500,
//		    "error": "Internal Server Error",
//		    "exception": "org.springframework.orm.jpa.JpaSystemException",
//		    "message": "A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance: com.example.algamoney.api.model.Pessoa.contatos; nested exception is org.hibernate.HibernateException: A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance: com.example.algamoney.api.model.Pessoa.contatos",
//		    "path": "/pessoas/22"
//		}
		// é preciso limpar os contatos e depois atualizar com os novos
		pessoaSalva.getContatos().clear();
		pessoaSalva.getContatos().addAll(pessoa.getContatos());
		pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));
		
		// os parâmetro "codigo" e "contatos" serve para que o BeanUtils.copyProperties ignore estes atributos.
		BeanUtils.copyProperties(pessoa,pessoaSalva,"codigo", "contatos");
		
//		System.out.println(">> Código " + pessoaSalva.get().getCodigo());
//		System.out.println(">> Nome " + pessoaSalva.get().getNome());
//		System.out.println(">> Logradouro " + pessoaSalva.get().getEndereco().getLogradouro());
		
		return pessoaRepository.save(pessoaSalva);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {	
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}
	
	public Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Pessoa pessoa = pessoaRepository.findOne(codigo);
		if (pessoa == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return pessoa;
	}
	
}
