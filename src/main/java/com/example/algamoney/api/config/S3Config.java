	package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Configuration
@PropertySource(value = { "file:\\C:\\opt\\.angular-s3.properties" }, ignoreResourceNotFound = true)
public class S3Config {

	@Autowired
	private AlgamoneyApiProperty property;
	
	@Autowired
	private Environment env;
	
	@Bean
	public AmazonS3 amazonS3() {
		
//		System.out.println(">>>>> AWS_ACCESS_KEY_ID: "+ env.getProperty("AWS_ACCESS_KEY_ID"));
//		System.out.println(">>>>> AWS_SECRET_ACCESS_KEY: "+ env.getProperty("AWS_SECRET_ACCESS_KEY"));
		
//		System.out.println(">>>>> property.getS3().getAccessKeyId(): " + property.getS3().getAccessKeyId());
//		System.out.println(">>>>> property.getS3().getSecretAccessKey(): " + property.getS3().getSecretAccessKey());
		
//		// acesso das credenciais
//		AWSCredentials credenciais = new BasicAWSCredentials(
//				property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());
		
		// acesso das credenciais
		AWSCredentials credenciais = new BasicAWSCredentials(
				env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_SECRET_ACCESS_KEY"));
	
		// instanciação da classe de envio		
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credenciais))
				.withRegion(Regions.US_EAST_1)
				.build();
		
		if (!amazonS3.doesBucketExistV2(property.getS3().getBucket())) {
			
			// equivale a um diretório onde ficará os arquivos
			amazonS3.createBucket(
					new CreateBucketRequest(property.getS3().getBucket()));
			
			// regra de expiração do arquivo temporário criados dentro do Bucket
			// Todos os arquivos que tiverem a tag 'expirar' serão excluídos após 1 dia
			BucketLifecycleConfiguration.Rule regraExpiracao = 
					new BucketLifecycleConfiguration.Rule()
					.withId("Regra de expiração de arquivos temporários")
					.withFilter(new LifecycleFilter(
							new LifecycleTagPredicate(new Tag("expirar", "true"))))
					.withExpirationInDays(1)
					.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			// criando o objeto de configuração
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
					.withRules(regraExpiracao);
			
			// associando a regra de expiração com o Bucket criado
			amazonS3.setBucketLifecycleConfiguration(property.getS3().getBucket(), 
					configuration);
		}
		
		return amazonS3;
	}
}
