package com.auth.infrastructure.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    @Value("${access.token.ttl.ms}")
    private long accessTokenTTLMs;

    private final String CLAIM_USER_ID = "userId";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        DefaultOAuth2AccessToken oauth2AccessToken = (DefaultOAuth2AccessToken) accessToken;

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(CLAIM_USER_ID, userDetails.getId());

        oauth2AccessToken.setAdditionalInformation(additionalInfo);
        oauth2AccessToken.setExpiration(new Date(getAccessTokenExpirationMs()));

        return accessToken;
    }

    private long getAccessTokenExpirationMs() {
        return System.currentTimeMillis() + accessTokenTTLMs;
    }

}
