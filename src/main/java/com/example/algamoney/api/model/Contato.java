package com.example.algamoney.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

//import org.hibernate.validator.constraints.Email;
//import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "contato")
public class Contato {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	@NotEmpty
	private String nome;
	
	@Email
	@NotNull
	private String email;
	
	@NotEmpty
	private String telefone;
	
	// observe que a classe da entidade Contato.java também faz referência à classe Pessoa.java.
	// Foi colocado o @JsonIgnoreProperties em Pessoa.java (ver classe para mais detalhes) 
	// Para gravar a pessoa (via postman http://localhost:8080/pessoas), como a classe Contato.java
	// está com @JsonIgnoreProperties("pessoa"), faremos o seguinte procedimento para a gravação:
	// 1. em PessoaResource.java substituir Pessoa pessoaSalva = pessoaRepository.save(pessoa);
	//    por Pessoa pessoaSalva = pessoaService.salvar(pessoa);
	// 2. em PessoaService.java, criaremo o método public Pessoa salvar(Pessoa pessoa) para 
	//    alimentar o contato, lido em ao ser chamado via postman http://localhost:8080/pessoas
	//    e em seguida gravar o epositório pessoa : pessoaRepository.save(pessoa);
	// 3. retirar a injeção @NotNull da classe contato, a fim de que posso ficar nulo ao gravar a pessoa.
	// Obs: se não fizar isso, apresentará o seguinte erro como resposta da chamada do API:
//	{
//        "mensagemUsuario": "contatos[0].pessoa é obrigatório(a)",
//        "mensagemDesenvolvedor": "Field error in object 'pessoa' on field 'contatos[0].pessoa': rejected value [null]; codes [NotNull.pessoa.contatos[0].pessoa,NotNull.pessoa.contatos.pessoa,NotNull.contatos[0].pessoa,NotNull.contatos.pessoa,NotNull.pessoa,NotNull.com.example.algamoney.api.model.Pessoa,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [pessoa.contatos[0].pessoa,contatos[0].pessoa]; arguments []; default message [contatos[0].pessoa]]; default message [{0} é obrigatório(a)]"
//    }
//	@NotNull
	@ManyToOne
	@JoinColumn(name = "codigo_pessoa")
	private Pessoa pessoa;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contato other = (Contato) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
}