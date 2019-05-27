package com.example.algamoney.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Component
public class S3 {

	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	@Autowired
	AlgamoneyApiProperty property;
	
	@Autowired
	private AmazonS3 amazonS3;
		
	public String salvarTemporariamente(MultipartFile arquivo) {
		
		System.out.println(">>>>> S3 salvarTemporariamente");
		
		// criou a instância onde diz que o objecto pode ser lido
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		// criação dos metadados
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());
		
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		
		// criação da requisição para incluir o objeto (arquivo)
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(), 
					nomeUnico,
					arquivo.getInputStream(),
					objectMetadata).withAccessControlList(acl);
			
			// obs: o none da Tag deve ser igual ao definido na classe S3Config.java
			// Esta definição é quem diz que o arquivo é temporário e vai expirar em 1 dia de acordo com a 
			// definição em S3Config.java
			putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));
			
			// enviar a requisição para o S3
			amazonS3.putObject(putObjectRequest);
			
			// criar log para informação que o envio foi OK
			if (logger.isDebugEnabled()) {
				logger.debug("-->>> Arquivo {} enviado com sucesso para o S3", arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar lançar o arquivo para o AWS S3", e);
		}
	}
	
	// obs: as duas \\ diz que não importar se o protocolo é http ou thttps. Como a \ é um carater de escape, dentro de uma 
	// string colocamos a \ duplicada, logo fica \\\\
	public String configurarUrl(String objeto) {
		return "\\\\" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
	}

	public void salvar(String objeto) {
		// será preciso remover a tag que já está no arquivo no aws S3 para retirar o status de temporário.
		// Como a lista a ser passada é uma lista vazia, na prática a tag do arquivo será removida.
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto, 
				new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}
	
	public void remover(String objeto) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), objeto);
		
		amazonS3.deleteObject(deleteObjectRequest);
	}
	
	public void substituir(String objetoAntigo, String objetoNovo) {
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		salvar(objetoNovo);
	}
	
	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}


}
