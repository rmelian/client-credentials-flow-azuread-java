package com.webapi.Resource.Web.API;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private final JWTParser customJWTParser;


    public TokenProvider(JWTParser customJWTParser) {
        this.customJWTParser = customJWTParser;

        try {
            this.customJWTParser.configure(new JWTParserConfiguration(
                "https://login.microsoftonline.com/common/discovery/keys", // defined by AzureAD
                JWSAlgorithm.RS256, // defined by AzureAD
                "", // defined by AzureAD
                "")); //specific for this services. application id defined by AzureAD
        } catch (IllegalArgumentException e) {
            log.error("CustomJWTParserConfiguration initialization error : {}", e);
        }
    }


    public Authentication getAuthentication(String token) {

        JWTClaimsSet claimsSet = this.getClaims(token);

        String userPrincipalName = claimsSet.getClaim("upn").toString();
        String issuer = claimsSet.getClaim("iss").toString();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(issuer.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(userPrincipalName, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);

    }


    public boolean validateToken(String authToken) {
        return (this.getClaims(authToken) != null);
    }

    private JWTClaimsSet getClaims(String authToken) {
        JWTClaimsSet claimsSet = null;

        try {
            claimsSet = this.customJWTParser.getTokenClaims(authToken);

        } catch (MalformedURLException e) {
            log.error("Invalid JWK set remote URL:{}", e);
        } catch (ParseException e) {
            log.error("Unsupported JWT token: {}", e);
        } catch (BadJOSEException e) {
            log.error("Invalid JWT signature or encryption: {}", e);
        } catch (JOSEException e) {
            log.error("JWT signature or encryption general exception: {}", e);
        }

        return claimsSet;
    }
}
