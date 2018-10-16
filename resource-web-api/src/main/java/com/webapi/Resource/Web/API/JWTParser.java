package com.webapi.Resource.Web.API;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

@Component
public class JWTParser {

    private JWTParserConfiguration configuration;

    /**
     * Constructor
     *
     *
     */
    public JWTParser() {
    }

    /***
     * Set the configuraiton object
     * @param config
     */
    public void configure(JWTParserConfiguration config) {
        if (config == null) throw new IllegalArgumentException("Configuration object not set");

        configuration = config;
    }

    /**
     * Returns JWT processor instance
     *
     * @return
     * @throws MalformedURLException
     */
    public ConfigurableJWTProcessor getJwtProcessor()
        throws MalformedURLException {
        if (configuration == null) throw new IllegalStateException("Configuration object not set");

        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

        JWKSource keySource = new RemoteJWKSet(new URL(configuration.getJWKSetUrl()));
        JWSAlgorithm expectedJWSAlg = configuration.getJWSAlgorithm();
        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);

        jwtProcessor.setJWSKeySelector(keySelector);

        return jwtProcessor;
    }

    /**
     * Return JWT Claim set after the validation of the following token properties:
     * - JWT encoding correctness
     * - Signing algorithm used
     * - JWT signature against JWK source
     * - JWT expiration claim
     * - JWT nbf (not use before) claim
     * - JWT custom claims: issuer, subject, audience
     * Otherwise it will throw an exception
     *
     * @param authToken
     * @return
     * @throws MalformedURLException
     * @throws ParseException
     * @throws BadJOSEException
     * @throws JOSEException
     */
    public JWTClaimsSet getTokenClaims(String authToken)
        throws MalformedURLException, ParseException, BadJOSEException, JOSEException {
        if (configuration == null) throw new IllegalStateException("Configuration object not set");

        ConfigurableJWTProcessor jwtProcessor = this.getJwtProcessor();

        SecurityContext ctx = null; // optional context parameter, not required here

        JWTClaimsSet claimsSet = null;

        jwtProcessor.setJWTClaimsSetVerifier(this.getClaimsVerifier());

        claimsSet = jwtProcessor.process(SignedJWT.parse(authToken), ctx);

        return claimsSet;
    }

    /**
     * Return a custom claims verifier
     *
     * @return
     */
    private DefaultJWTClaimsVerifier getClaimsVerifier() {
        return new DefaultJWTClaimsVerifier() {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext context)
                throws BadJWTException {
                if (configuration == null) throw new IllegalStateException("Configuration object not set");

                super.verify(claimsSet, context);

//                String expectedIssuer = configuration.getExpectedIssuerClaim();
//                List<String> expectedAudience = new ArrayList<String>();
//                Collections.addAll(expectedAudience, configuration.getExpectedAudienceClaim().split("\\|"));
//
//                String issuer, subject;
//                List<String> audience;
//
//                issuer = claimsSet.getIssuer();
//                audience = claimsSet.getAudience();
//
//                if (!issuer.equals(expectedIssuer)) {
//                    throw new BadJWTException("Invalid token issuer");
//                }
//
//                if (audience == null || (!audience.get(0).equals(expectedAudience.get(0)) && !audience.get(0).equals(expectedAudience.get(1)))) {
//                    throw new BadJWTException("Invalid token audience");
//                }
            }
        };
    }
}
