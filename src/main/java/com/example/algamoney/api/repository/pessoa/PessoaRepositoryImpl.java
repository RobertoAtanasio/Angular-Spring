package com.example.algamoney.api.repository.pessoa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.model.Pessoa_;

public class PessoaRepositoryImpl implements PessoaRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Pessoa> listarPessoaOrderByNome(String nome, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);
		Order order = builder.asc(root.get("nome"));
		
		this.criarRestricoes(nome, builder, criteria, root);
		criteria.orderBy(order);
		TypedQuery<Pessoa> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(pageable, query);
		List <Pessoa> lista = query.getResultList();
		Page<Pessoa> pagina = new PageImpl<>(lista, pageable, this.total(nome));
		
		manager.close();
		
		return pagina;
	}

	private void criarRestricoes(String nome, CriteriaBuilder builder, CriteriaQuery<?> criteria,
		Root<Pessoa> root) {
		List<Predicate> condicoes = new ArrayList<>();
		
		if (!StringUtils.isEmpty(nome)) {
			condicoes.add(builder.like(
					builder.lower(root.get(Pessoa_.nome)), "%" + nome.toLowerCase() + "%"));
		}
				
		Predicate[] predicates = condicoes.toArray(new Predicate[condicoes.size()]);
		criteria.where(predicates);
	}
	
	private void adicionarRestricoesDePaginacao(Pageable pageable, TypedQuery<?> query) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	
	private Long total(String nome) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Pessoa> root = criteria.from(Pessoa.class);
		
		this.criarRestricoes(nome, builder, criteria, root);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
