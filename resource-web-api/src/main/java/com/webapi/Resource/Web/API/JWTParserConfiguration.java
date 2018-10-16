package com.webapi.Resource.Web.API;

import com.nimbusds.jose.JWSAlgorithm;

public class JWTParserConfiguration {

    private String JWKSetUrl;
    private JWSAlgorithm JWSAlgorithm;
    private String expectedIssuerClaim;
    private String expectedAudienceClaim;

    /**
     * Constructor
     *
     * @param JWKSetUrl
     * @param JWSAlgorithm
     * @param expectedIssuerClaim
     * @param expectedAudienceClaim
     */
    public JWTParserConfiguration(String JWKSetUrl,
                                  JWSAlgorithm JWSAlgorithm,
                                  String expectedIssuerClaim,
                                  String expectedAudienceClaim) {
        this.JWKSetUrl = JWKSetUrl;
        this.JWSAlgorithm = JWSAlgorithm;
        this.expectedIssuerClaim = expectedIssuerClaim;
        this.expectedAudienceClaim = expectedAudienceClaim;
    }

    public String getJWKSetUrl() {
        return JWKSetUrl;
    }

    public com.nimbusds.jose.JWSAlgorithm getJWSAlgorithm() {
        return JWSAlgorithm;
    }

    public String getExpectedIssuerClaim() {
        return expectedIssuerClaim;
    }


    public String getExpectedAudienceClaim() {
        return expectedAudienceClaim;
    }
}
