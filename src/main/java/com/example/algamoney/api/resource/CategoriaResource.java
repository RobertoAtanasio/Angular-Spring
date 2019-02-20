package com.example.algamoney.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public List<Categoria> listar() {  
		// A inclusão da permissão acima, faz com que a permissão em ResourceServerConfig.java fique sem validade.
		// Para a permissão lá ter validade, basta não injetar esse @PreAuthorize 
		return categoriaRepository.findAll();
	}

//	@GetMapping
//	public ResponseEntity<?> listar() {
//		// o ResponseEntity.notFound().build() retorna erro = 404 Not Found e build faz retornar um ResponseEntity
//		// o ResponseEntity.notFound().build() retorna erro = 204 No Content e build faz retornar um ResponseEntity
//		List<Categoria> categorias = categoriaRepository.findAll(); 
////		return !categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.notFound().build();
//		return !categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.noContent().build();
//	}
	
	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)  // ao terminar, retornar status de CREATED - 201
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
//		//--- procedimento para retornar o código da categoria salva para a URI
//		
//		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
//					.buildAndExpand(categoriaSalva.getCodigo()).toUri();
//		response.setHeader("Location", uri.toASCIIString());
//		
//		//--- Obs.: após o comando abaixo, a injeção @ResponseStatus no método não será mais preciso,
//		//	  pois o 'created(uri)...' já passo o status.
//		//    Retornar o objeto criado. Para isso o retorno da função será ResponseEntity<Categoria>
//		
//		return ResponseEntity.created(uri).body(categoriaSalva);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
		
	}
	
//	@GetMapping("/{codigo}")
//	public Optional<Categoria> buscarPeloCodigo (@PathVariable Long codigo) {
//		return categoriaRepository.findById(codigo);
//	}

	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public ResponseEntity<?> buscarPeloCodigo (@PathVariable Long codigo) {
		Categoria categoriaLida = categoriaRepository.findOne(codigo);
		//--- caso não encontre, retornará erro 404 Not Found
		return categoriaLida != null ? ResponseEntity.ok(categoriaLida) : ResponseEntity.notFound().build();
	}

}