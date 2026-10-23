# 04 · Deploy e CI

- `Dockerfile` em dois estágios: build com JDK 21 e `mvnw package`; imagem final só com JRE 21 e usuário não-root.
- `JWT_SECRET` precisa ser passada em tempo de execução (`docker run -e JWT_SECRET=...`), nunca embutida na imagem.
- `.github/workflows/ci.yml`: job `test` roda `./mvnw -B verify` com JDK 21 (Temurin); job `docker` só roda depois dos testes e faz o build da imagem.
