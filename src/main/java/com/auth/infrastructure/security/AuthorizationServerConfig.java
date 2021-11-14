package com.auth.infrastructure.security;

import java.security.KeyPair;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Import(AuthorizationServerEndpointsConfiguration.class)
@Configuration
public class AuthorizationServerConfig implements AuthorizationServerConfigurer {

    @Value("${oauth2.gateway.client-id}")
    private String gatewayClientId;

    @Value("${oauth2.gateway.client-secret}")
    private String gatewayClientSecret;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final KeyPair keyPair;
    private final UserDetailsService userDetailsService;
    private final CustomTokenEnhancer customTokenEnhancer;

    @Autowired
    public AuthorizationServerConfig(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
            KeyPair keyPair, UserDetailsService userDetailsService, CustomTokenEnhancer customTokenEnhancer) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.keyPair = keyPair;
        this.userDetailsService = userDetailsService;
        this.customTokenEnhancer = customTokenEnhancer;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .checkTokenAccess("isAuthenticated()")
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(gatewayClientId)
                .secret(passwordEncoder.encode(gatewayClientSecret))
                .authorizedGrantTypes(
                        AuthorizationGrantType.PASSWORD.getValue())
                .scopes("read", "write")
                .autoApprove(true);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(this.authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenEnhancer(getTokenEnhancerChain())
                .tokenStore(tokenStore());
    }

    private TokenEnhancerChain getTokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(customTokenEnhancer, accessTokenConverter()));
        return tokenEnhancerChain;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(this.keyPair);
        return converter;
    }

}
