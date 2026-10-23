# 02 · Autenticação

- `POST /auth/register`: cria usuário com papel `USER`. Senha guardada com **BCrypt** (`BCryptPasswordEncoder`). E-mail já existente responde 409.
- `POST /auth/login`: valida credenciais e devolve `accessToken` (JWT HS256) com expiração curta. Credenciais inválidas respondem 401.
- `JwtService` assina e valida o token com a chave de `JWT_SECRET`. A chave tem que ter pelo menos 32 caracteres, exigência do HS256; a validação roda no startup (`JwtProperties`, `@Validated` + `@NotBlank` + `@Size`).
- `AdminSeeder`: cria um ADMIN no startup se `ADMIN_EMAIL` e `ADMIN_PASSWORD` estiverem definidos. O registro público nunca cria ADMIN.
- Criptografia: só bibliotecas padrão (BCrypt do Spring Security, jjwt). Nada de código criptográfico próprio.
