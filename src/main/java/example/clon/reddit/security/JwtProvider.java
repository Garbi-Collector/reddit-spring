package example.clon.reddit.security;

import example.clon.reddit.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static io.jsonwebtoken.Jwts.parser;


@Service
public class JwtProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init(){
        try{
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e){
            throw new SpringRedditException("Exception Ocurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        org.springframework.security.core.userdetails.User principal =(User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private Key getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
            throw new SpringRedditException("Exception Ocurred while retrieving public key from keysotre");
        }
    }

    public boolean validateToken(String jwt) {
        try {
            // Usar parserBuilder() para obtener un JwtParser
            Jwts.parserBuilder()
                    .setSigningKey(getPublicKey()) // Establecer la clave pública para la verificación
                    .build() // Construir el parser
                    .parseClaimsJws(jwt); // Verificar el JWT

            return true; // Si no lanza excepciones, el token es válido
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Si el token no es válido o tiene problemas
        }
    }


    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        }catch (KeyStoreException e){
            throw new SpringRedditException("Exception Ocurred while retrieving public key");
        }
    }

    public String getUsernameFromJwt(String token){
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
