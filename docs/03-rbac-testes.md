# 03 · RBAC e testes

- `JwtAuthFilter` lê `Authorization: Bearer ...`, valida o token e monta a autenticação com os papéis das claims. Token inválido, adulterado ou expirado segue sem autenticação, e a rota protegida responde 401.
- `SecurityConfig` habilita `@EnableMethodSecurity`. `GET /me` exige `hasRole("USER")`; `GET /admin/usuarios` usa `@PreAuthorize("hasRole('ADMIN')")`.
- Testes em `AuthRbacTests` com MockMvc (6 casos): 401 sem token, 403 com USER na rota de ADMIN, 200 com ADMIN, token expirado (401), token adulterado (401) e o fluxo register → login → `/me`.
- Limitação conhecida: o token de ADMIN dos testes é emitido com papel ADMIN, sem usuário no banco; o filtro confia nas claims.
