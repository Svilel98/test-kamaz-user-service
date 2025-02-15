package dev.nyura.kamaz.user.security;

import dev.nyura.kamaz.user.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public Proccesor getAccessTokenProccesor() {
        return new Proccesor(jwtProperties.getAccessToken().getSecret(), jwtProperties.getAccessToken().getExpiration());
    }

    public Proccesor getRefreshTokenProccesor() {
        return new Proccesor(jwtProperties.getRefreshToken().getSecret(), jwtProperties.getRefreshToken().getExpiration());
    }

    @RequiredArgsConstructor
    public static class Proccesor {

        private final String secretKey;

        private final Duration expiration;


        public String generateToken(UserDetails userDetails) {
            return Jwts.builder()
                    .setClaims(new HashMap<>())
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        }

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public boolean isTokenValid(String token, UserDetails userDetails) {
            return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return claimsResolver.apply(claims);
        }
    }
}
