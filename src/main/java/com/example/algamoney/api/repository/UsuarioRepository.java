package com.example.algamoney.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	public Optional<Usuario> findByEmail(String email);
	
	// o nome Permissoes abaixo e o campo private List<Permissao> permissoes em Usuario.java
	// e o nome Descricao é o campo desccricao em Permissao.java
	public List<Usuario> findByPermissoesDescricao(String permissaoDescricao);
	
}