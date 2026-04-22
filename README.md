# 🎨 Image Color Inverter

> Inverte as cores de imagens via API REST — feito por aprendizado e diversão.

---

## Motivação

Este projeto foi criado **para aprendizado e diversão**. A ideia era explorar na prática como integrar **Rust compilado para WebAssembly (WASM)** dentro de uma aplicação **Java/Spring Boot**, usando o runtime WASM [Chicory](https://github.com/dylibso/chicory) para executar o módulo Rust diretamente na JVM — sem JNI, sem processos externos.

É um experimento simples, mas que combina várias tecnologias interessantes ao mesmo tempo.

---

## O que faz

Recebe uma imagem via HTTP, inverte todos os bytes de pixels (cada canal de cor vira `255 - valor`) e retorna a imagem invertida. O processamento pesado de pixels acontece em código Rust compilado para WASM.

---

## Funcionalidades

- API REST com um endpoint `POST /image/invert`
- Suporta os formatos que o Java (`ImageIO`) consegue ler (PNG, JPEG, BMP, etc.)
- Remove canal alfa antes de processar (fundo branco por padrão)
- Processamento de pixels via **Rust → WASM → Chicory (JVM)**
- Devolve a imagem invertida no mesmo formato da entrada

---

## Requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java | 21 |
| Maven (ou `./mvnw`) | 3.9+ |
| Rust / Cargo | stable |
| Target WASM | `wasm32-unknown-unknown` |

Para instalar o target WASM do Rust:

```bash
rustup target add wasm32-unknown-unknown
```

---

## Como compilar e executar

### 1. Compilar o módulo Rust para WASM

```bash
cd image-processor-wasm
cargo build --release --target wasm32-unknown-unknown
```

O arquivo `.wasm` gerado ficará em:
```
image-processor-wasm/target/wasm32-unknown-unknown/release/demo.wasm
```

### 2. Subir a aplicação Spring Boot

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

Ou, para gerar o `.jar` e rodar:

```bash
./mvnw package
java -jar target/img-0.0.1-SNAPSHOT.jar
```

A aplicação sobe na porta **8080** por padrão.

---

## Como usar

### Endpoint

```
POST /image/invert
Content-Type: multipart/form-data
```

### Exemplo com `curl`

```bash
curl -X POST http://localhost:8080/image/invert \
  -F "image=@foto.png" \
  --output foto_invertida.png
```

A resposta é a imagem com as cores invertidas, no mesmo formato da entrada.

---

## Exemplos

**Antes:**

Qualquer imagem normal — um gato, um pôr do sol, um print de terminal.

**Depois:**

Os canais RGB de cada pixel são invertidos: `R' = 255 - R`, `G' = 255 - G`, `B' = 255 - B`. O que era claro fica escuro e vice-versa.

**Cor original → Cor invertida:**
- Branco `#FFFFFF` → Preto `#000000`
- Vermelho `#FF0000` → Ciano `#00FFFF`
- Azul `#0000FF` → Amarelo `#FFFF00`

---

## Estrutura do projeto

```
image-color-inverter/
├── image-processor-wasm/       # Módulo Rust compilado para WASM
│   ├── src/lib.rs               # Lógica de inversão de pixels (alloc, dealloc, invertImage)
│   └── Cargo.toml
├── src/
│   └── main/java/com/bruno/demo/
│       ├── ImgApplication.java          # Entry point Spring Boot
│       ├── ImageMapper.java             # Utilitário: formato e conversão de imagem
│       ├── ImagePreprocessor.java       # Remove canal alfa da imagem
│       ├── controller/
│       │   └── ImageController.java     # Endpoint POST /image/invert
│       └── service/
│           ├── ImageService.java        # Orquestra o fluxo de inversão
│           └── WasmService.java         # Carrega e executa o módulo WASM via Chicory
├── pom.xml                      # Dependências Maven (Spring Boot + Chicory)
└── mvnw                         # Maven Wrapper
```

---

## Performance (futuro)

O projeto hoje é simples e suficiente para o objetivo de aprendizado. Se um dia for necessário escalar, estas seriam as três otimizações mais impactantes:

### 1. Paralelismo (multi-thread no CPU)

Cada pixel é independente dos outros — não existe dependência de dados entre eles. Isso significa que a imagem pode ser dividida em faixas horizontais (por exemplo, N blocos de linhas) e cada bloco processado por uma thread separada.

Com `ForkJoinPool` ou `ExecutorService` do Java, ou com `rayon` no lado Rust, é possível usar todos os núcleos do processador sem mudar a lógica de inversão. O cuidado necessário é garantir que cada thread escreva numa região exclusiva do buffer, evitando sincronização desnecessária.

Para imagens grandes, o ganho seria quase linear até o número de núcleos disponíveis.

### 2. SIMD (vetorização de pixels)

Em vez de processar 1 byte por vez, instruções SIMD (SSE2/AVX2 em x86, NEON em ARM) permitem operar em 16, 32 ou 64 bytes por instrução — invertendo vários pixels ao mesmo tempo.

A operação `255 - x` em cada byte é exatamente o tipo de loop que compiladores conseguem vetorizar automaticamente quando o código está bem estruturado (buffer contíguo, sem aliasing). No lado Rust, isso pode ser feito de forma explícita com `std::simd` (nightly) ou através de crates como `wide`, garantindo que a vetorização aconteça independente do nível de otimização do compilador.

O ganho potencial é de 16x a 32x no throughput do loop de inversão em relação à versão escalar.

### 3. Rust via JNI/FFI (hot path nativo)

A solução atual já usa Rust — mas via WASM dentro da JVM (Chicory). Isso adiciona uma camada de interpretação/compilação JIT do WASM. Para máxima performance, o hot path poderia ser movido para uma biblioteca nativa (`.so`/`.dll`) chamada diretamente via JNI ou Project Panama (FFI moderno do Java).

O fluxo seria: Java prepara um `DirectByteBuffer` com os pixels → passa o ponteiro e o tamanho para o código Rust → Rust processa in-place com acesso direto à memória → Java lê o resultado sem cópias extras.

O ganho vem da eliminação da overhead do runtime WASM, do acesso direto à memória sem marshalling, e da liberdade de usar SIMD e threads nativas com total controle. O cuidado principal é o custo de travessia JNI — por isso a chamada deve ser feita em blocos grandes, não por pixel.

---

## Licença

Licença não definida. Este é um projeto de aprendizado e não há restrições formais de uso no momento.
