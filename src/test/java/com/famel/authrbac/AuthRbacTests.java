package com.famel.authrbac;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "app.jwt.secret=" + AuthRbacTests.SECRET)
@AutoConfigureMockMvc
class AuthRbacTests {
    static final String SECRET = "test-secret-test-secret-test-secret-123456";

    @Autowired MockMvc mvc;
    @Autowired JwtService jwt;

    String bearer(String t) { return "Bearer " + t; }

    @Test void semTokenRetorna401() throws Exception {
        mvc.perform(get("/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/admin/usuarios")).andExpect(status().isUnauthorized());
    }

    @Test void userNaRotaAdminRetorna403() throws Exception {
        mvc.perform(get("/admin/usuarios").header("Authorization", bearer(jwt.issue("u@x.com", AppUser.Role.USER))))
                .andExpect(status().isForbidden());
    }

    @Test void adminNaRotaAdminRetorna200() throws Exception {
        mvc.perform(get("/admin/usuarios").header("Authorization", bearer(jwt.issue("a@x.com", AppUser.Role.ADMIN))))
                .andExpect(status().isOk());
    }

    @Test void tokenExpiradoRetorna401() throws Exception {
        String expired = Jwts.builder().subject("u@x.com").claim("role", "USER")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))).compact();
        mvc.perform(get("/me").header("Authorization", bearer(expired))).andExpect(status().isUnauthorized());
    }

    @Test void tokenAdulteradoRetorna401() throws Exception {
        String[] p = jwt.issue("u@x.com", AppUser.Role.USER).split("\\.");
        String forgedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"u@x.com\",\"role\":\"ADMIN\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        // payload trocado para ADMIN mantendo a assinatura original
        String forged = p[0] + "." + forgedPayload + "." + p[2];
        mvc.perform(get("/admin/usuarios").header("Authorization", bearer(forged))).andExpect(status().isUnauthorized());
        // assinatura corrompida
        String badSig = p[0] + "." + p[1] + "." + "A".repeat(p[2].length());
        mvc.perform(get("/me").header("Authorization", bearer(badSig))).andExpect(status().isUnauthorized());
    }

    @Test void registerLoginMe() throws Exception {
        String body = "{\"email\":\"novo@x.com\",\"password\":\"senha-forte-1\"}";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        String res = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = res.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");
        mvc.perform(get("/me").header("Authorization", bearer(token))).andExpect(status().isOk());
    }
}
