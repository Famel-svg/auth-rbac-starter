# 01 · Build e configuração base

- Spring Boot **3.5.6** (parent no `pom.xml`), Java **21** (`release 21`).
- Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/`) para não depender de Maven instalado.
- Dependências: `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-data-jpa`, H2 (runtime) e `jjwt` 0.12.6.
- `application.yml`: segredo JWT **sem valor padrão** (`${JWT_SECRET}`), expiração em `JWT_EXPIRATION_SECONDS` (padrão 900 s).
- `PROMPT.md` fica no `.gitignore`: é o prompt de origem, não faz parte do repositório.

- `mvnw` versionado com bit de execução (`100755`), necessário para rodar no CI Linux e no Docker.
