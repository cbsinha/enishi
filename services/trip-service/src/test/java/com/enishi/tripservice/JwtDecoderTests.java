
package com.enishi.tripservice;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwsHeader;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtDecoderTests {

    private static final String ISSUER =
            "https://wondrous-cod-4957.clerk.accounts.dev";

    private KeyPair keyPair;
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        keyPair = generator.generateKeyPair();

        RSAKey rsaKey = new RSAKey.Builder(
                (RSAPublicKey) keyPair.getPublic())
                .privateKey((java.security.interfaces.RSAPrivateKey)
                        keyPair.getPrivate())
                .keyID("test-key")
                .algorithm(JWSAlgorithm.RS256)
                .build();

        jwtEncoder = new NimbusJwtEncoder(
                new ImmutableJWKSet<>(new JWKSet(rsaKey))
        );

        jwtDecoder = NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) keyPair.getPublic())
                .build();

        ((NimbusJwtDecoder) jwtDecoder).setJwtValidator(
                JwtValidators.createDefaultWithIssuer(ISSUER)
        );
    }

    private String createToken(
            String issuer,
            Instant expiration,
            KeyPair signingKey) {

        RSAKey rsaKey = new RSAKey.Builder(
                (RSAPublicKey) signingKey.getPublic())
                .privateKey((java.security.interfaces.RSAPrivateKey)
                        signingKey.getPrivate())
                .keyID("test-key")
                .algorithm(JWSAlgorithm.RS256)
                .build();

        JwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableJWKSet<>(new JWKSet(rsaKey))
        );

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("user_test123")
                .issuedAt(expiration.minusSeconds(3600))
                .expiresAt(expiration)
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .keyId("test-key")
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    @Test
    void validTokenShouldBeAccepted() {

        String token = createToken(
                ISSUER,
                Instant.now().plusSeconds(300),
                keyPair
        );

        Jwt decoded = jwtDecoder.decode(token);

        assertEquals("user_test123", decoded.getSubject());
        assertEquals(ISSUER, decoded.getIssuer().toString());
    }

    @Test
    void wrongIssuerShouldBeRejected() {

        String token = createToken(
                "https://wrong-issuer.example.com",
                Instant.now().plusSeconds(300),
                keyPair
        );

        assertThrows(
                JwtException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    @Test
    void expiredTokenShouldBeRejected() {

        String token = createToken(
                ISSUER,
                Instant.now().minusSeconds(60),
                keyPair
        );

        assertThrows(
                JwtException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    @Test
    void invalidSignatureShouldBeRejected() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        KeyPair differentKeyPair = generator.generateKeyPair();

        String token = createToken(
                ISSUER,
                Instant.now().plusSeconds(300),
                differentKeyPair
        );

        assertThrows(
                JwtException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    @Test
    void malformedTokenShouldBeRejected() {

        assertThrows(
                JwtException.class,
                () -> jwtDecoder.decode("this-is-not-a-valid-jwt")
        );
    }
}
