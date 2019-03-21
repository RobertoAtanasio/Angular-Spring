package com.example.algamoney.api.resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.model.Teste;
import com.example.algamoney.api.repository.TestesRepository;

@RestController
@RequestMapping("/testes")
public class TesteResource {

	@Autowired
	private TestesRepository testesRepository;
	
	@GetMapping
	public List<Teste> listar() {  
		
		Teste teste = new Teste();
		LocalDate dataAux = LocalDate.now();
		LocalTime hora = LocalTime.now();
		
		System.out.println(">>>> LocalDate: " + dataAux);
		System.out.println(">>>> LocalTime: " + hora);
		
		teste.setDataHora(LocalDateTime.of(dataAux, hora));
		
		System.out.println(">>>> Data Hora: " + teste.getDataHora());
		
		testesRepository.save(teste);
		
		return testesRepository.findAll();
	}
}
