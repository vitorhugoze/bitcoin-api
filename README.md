# bitcoin-api

API REST em Spring Boot para consulta histórica de preços de Bitcoin, com pipeline CI/CD automatizado via GitHub Actions e Jenkins.

---

## Tecnologias

- Java + Spring Boot
- Docker — containerização
- GitHub Actions — build e publicação no GHCR
- Jenkins — deploy automático na instância

---

## Endpoints

A API roda em `http://163.176.214.184:12055`.

> Qualquer pessoa pode fazer requisições para este endpoint público se quiser testar a API.

### GET /prices

Retorna os registros históricos de preço do Bitcoin. Aceita filtro de período via headers.

| Header | Obrigatório | Formato |
|--------|-------------|---------|
| `iniDate` | não | `AAAA-MM-DD` |
| `finDate` | não | `AAAA-MM-DD` |

```
curl http://163.176.214.184:12055/prices \
  -H "iniDate: 2024-01-01" \
  -H "finDate: 2024-03-31"
```

Erros tratados:
- `400` — formato de data inválido
- `500` — falha na leitura da base de dados

---

## Fluxo CI/CD

```
push main → GitHub Actions → build multi-arch → GHCR → Jenkins → deploy
```

**GitHub Actions** compila e publica a imagem no GitHub Container Registry (`ghcr.io/vitorhugoze/bitcoin-api:latest`) a cada push na `main`.

**Jenkins** derruba o container anterior, busca imagem atualizada e sobe o novo automaticamente.

---

## Pipeline GitHub Actions

```yaml
name: Build and Publish to GHCR

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    permissions:
      contents: read
      packages: write

    steps:
    - name: Checkout código
      uses: actions/checkout@v4

    - name: Set up QEMU
      uses: docker/setup-qemu-action@v3

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3

    - name: Log in to GHCR
      uses: docker/login-action@v3
      with:
        registry: ghcr.io
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        file: ./Dockerfile
        platforms: linux/amd64,linux/arm64
        push: true
        no-cache: true
        tags: ghcr.io/vitorhugoze/bitcoin-api:latest
```

---

## Pipeline Jenkins

```groovy
pipeline {
    agent any

    stages {
        stage('Remove container existente') {
            steps {
                sh 'docker rm -f api-bitcoin || true'
            }
        }

        stage('Apaga imagem existente') {
            steps {
                sh 'docker rmi ghcr.io/vitorhugoze/bitcoin-api:latest || true'
            }
        }

        stage('Busca imagem mais recente') {
            steps {
                sh 'docker pull ghcr.io/vitorhugoze/bitcoin-api:latest'
            }
        }

        stage('Inicia container') {
            steps {
                sh 'docker run -d --name api-bitcoin -p 12055:12055 ghcr.io/vitorhugoze/bitcoin-api:latest'
            }
        }
    }
}
```