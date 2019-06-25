package com.example.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		System.out.println(">>>>>>>  4   ClientDetailsServiceConfigurer >>>>>>>>>>>>>>>>");
		
		clients.inMemory()
			.withClient("angular")
			.secret("$2a$10$Nyrj9biofcOvj9PwIn7bsu70ju17mxG9.DUL4OUi22FWUGBqwLGWO")		// angular0
			.scopes("read", "write")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(60000)
			.refreshTokenValiditySeconds(18000)
		.and()
			.withClient("mobile")
			.secret("$2a$10$S.U0DxO.Fbti2hbuadAIWu.cW.a3VTqPLR/6Zb7VEFEjeQRoXglky")		// mobile0
			.scopes("read")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(600)
			.refreshTokenValiditySeconds(3600 * 24);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		System.out.println(">>>>>>>  3   AuthorizationServerEndpointsConfigurer >>>>>>>>>>>>>>>>");

		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
		.tokenStore(tokenStore())
		.tokenEnhancer(tokenEnhancerChain)
		.reuseRefreshTokens(false)
		.userDetailsService(userDetailsService)
		.authenticationManager(authenticationManager);
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		
		System.out.println(">>>>>>>  0   JwtAccessTokenConverter >>>>>>>>>>>>>>>>");
		
		JwtAccessTokenConverter accessToken = new JwtAccessTokenConverter();
		accessToken.setSigningKey("algaworks");	// senha que fica na VERIFY SIGNATURE do token
		
		return accessToken;
	}

	@Bean
	public TokenStore tokenStore() {
		
		System.out.println(">>>>>>>  1   TokenStore >>>>>>>>>>>>>>>>");
		
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		
		System.out.println(">>>>>>>  2   TokenEnhancer >>>>>>>>>>>>>>>>");
		
	    return new CustomTokenEnhancer();
	}
	
}