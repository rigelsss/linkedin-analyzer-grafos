# Dicas para Gravar o Vídeo

O professor pediu explicação **linha por linha**, demonstrando que você entende o que foi produzido.
Este guia te ajuda a gravar com confiança e sem enrolação.

---

## Antes de Gravar

- [ ] Ler o `explicacao-do-projeto.md` pelo menos uma vez com calma
- [ ] Abrir o projeto no IntelliJ e deixar as abas já na ordem que vai seguir
- [ ] Rodar o `LinkedInApp` e confirmar que a saída está correta
- [ ] Fechar abas desnecessárias do navegador e desligar notificações
- [ ] Testar o microfone — áudio ruim derruba a qualidade mais que vídeo ruim
- [ ] Ter um copo d'água por perto

---

## Ordem Sugerida para o Vídeo

### 1. Apresentação (30 segundos)
> "Olá, meu nome é [nome], RGM [número], e este é o projeto final de Teoria dos Grafos — o LinkedIn Analyzer."

Mostre rapidamente a estrutura de pastas no IntelliJ.

---

### 2. Explicar o Problema (1–2 minutos)
Abra o `infosiniciais.txt` ou fale sem abrir:

- A rede é um **grafo não-direcionado e ponderado**
- Vértices = pessoas, arestas = conexões, pesos = afinidade (1 = muito próximos, 5+ = pouco)
- O objetivo é construir um analisador com 4 funcionalidades

---

### 3. `Vertice.java` (2 minutos)
- Mostre os campos: `nome`, `grau`, `adjacencias`, `adjacentes`
- Explique que `adjacencias` são os vizinhos para onde "sai" e `adjacentes` de onde "entra"
- Para grafos não-dirigidos, os dois lados são populados para os dois vértices
- Lombok: diga que `@Getter` e `@Setter` geram os métodos automaticamente — sem isso teria getters manuais

---

### 4. `Aresta.java` (1 minuto)
- Mostra origem, destino e peso
- O peso é o valor de afinidade

---

### 5. `Grafo.java` — estrutura base (3 minutos)
- Construtor: `new Grafo(false, true)` — não-dirigido, ponderado
- `adicionaVertices`: cria objetos `Vertice` e incrementa `ordem`
- `addAresta`: chama `criaAresta` → aumenta grau → chama `resolveAdjacencias`
- **`resolveAdjacencias`**: explique que para não-dirigido, adiciona o vizinho nas `adjacencias` E nos `adjacentes` dos dois lados — isso é o que permite percorrer nos dois sentidos

---

### 6. `Grafo.java` — Dijkstra (5–7 minutos) ← parte mais importante

Esta é a etapa que o professor mais vai avaliar. Vá devagar.

**Introdução:**
> "O enunciado pede um algoritmo de menor caminho ponderado. Implementei o Dijkstra diretamente na classe Grafo."

**Por que Dijkstra?**
- BFS encontra o menor número de arestas, mas ignora os pesos
- Greedy (já existia no código do professor) escolhe o melhor passo a passo localmente, mas pode errar globalmente
- Dijkstra considera o custo total acumulado desde a origem e garante o resultado ótimo

**Explique linha por linha:**

```java
record Entrada(Vertice vertice, int custo) {}
```
> "Criei um record local para guardar o par vértice + custo acumulado dentro da fila de prioridade."

```java
Map<Vertice, Integer> distancias = new HashMap<>();
```
> "Esse mapa guarda a menor distância conhecida da origem até cada vértice. Começa com infinito para todos e zero para a origem."

```java
Map<Vertice, Vertice> anteriores = new HashMap<>();
```
> "Rastreia o caminho: para cada vértice, guardo de onde eu vim. No final, uso isso para reconstruir a rota."

```java
PriorityQueue<Entrada> fila = new PriorityQueue<>(Comparator.comparingInt(Entrada::custo));
```
> "Fila de prioridade ordenada pelo custo — sempre processa primeiro o vértice mais barato de alcançar."

**Loop principal:** explique o `poll`, o check de visitados, e o relaxamento da aresta (`novaDistancia < distancias.get(vizinho)`).

**Reconstrução:** mostre que percorre o mapa `anteriores` de trás para frente, inserindo no início da lista.

**Demonstre no cenário:**
> "Ana para Fernanda: a rota Ana→Daniela→Fernanda custa 13. A rota Ana→Bruno→Eduardo→Fernanda custa 3. O Dijkstra encontra a de custo 3."

---

### 7. `ResultadoCaminho.java` (1 minuto)
- É um `record` que empacota caminho (lista de nomes) e custo (inteiro)
- `encontrou()` retorna `false` se o custo for -1 (sem caminho)

---

### 8. `LinkedInAnalyzer.java` (6–8 minutos)

**Missão 2 — sugerirConexoes:**
- Coleta os contatos diretos em um `Set` (busca O(1))
- Para cada amigo, percorre os amigos dele e filtra: não pode ser o próprio usuário, não pode ser contato direto
- `Map.merge(candidato, 1, Integer::sum)` — incrementa o contador de amigos em comum
- Ordena por valor decrescente com `comparingByValue().reversed()`

**Missão 3 — grauDeSeparacao:**
- Explique BFS vs DFS: BFS expande em camadas, garante o menor número de passos
- `Map<Vertice, Integer> niveis` serve como visitados e também guarda a distância
- Quando encontra o destino, retorna o nível atual + 1
- Retorna -1 se o BFS esgota sem encontrar

**Missão 4 — rotaDeMaiorAfinidade:**
- Delega ao `grafo.dijkstra()` — o algoritmo já está encapsulado na classe `Grafo`
- Explique por que faz sentido o Dijkstra estar no `Grafo` (é um algoritmo de grafo, não de análise de negócio)

**Missão 5 — mapearGruposIsolados:**
- Itera por todos os vértices; vértice já visitado → já tem grupo, pula
- Para cada novo vértice não visitado, faz BFS para mapear todo o componente
- Ao final, cada posição da lista `grupos` é um componente conexo

---

### 9. `LinkedInApp.java` (2–3 minutos)
- Mostre a criação do grafo e adição das conexões do cenário
- Execute ao vivo (`Run` no IntelliJ) e mostre a saída no console
- Comente cada bloco de saída brevemente

---

### 10. Encerramento (30 segundos)
> "Esse foi o LinkedIn Analyzer. Implementamos Dijkstra para maior afinidade, BFS para grau de separação e para mapear os grupos isolados, e travessia de adjacências para sugestão de conexões. Obrigado."

---

## Dicas de Apresentação

**Tom de voz:**
- Fale como se estivesse explicando para um colega, não lendo um roteiro
- Vá devagar no Dijkstra — é o coração do projeto

**Sobre erros:**
- Se travar, pause, respire e continue — não precisa recomeçar do zero
- Se errar o nome de algo, corrija na hora: "na verdade é X" — isso mostra que você sabe

**Sobre o código:**
- Não leia variáveis letra por letra — explique o que elas representam
- Prefira dizer "esse mapa guarda a menor distância até cada vértice" do que "distancias dot get vizinho"

**Duração sugerida:** 20 a 30 minutos é o ideal. Menos que 15 pode parecer superficial.

---

## Upload no YouTube

1. No YouTube Studio, faça upload do arquivo de vídeo
2. Título sugerido: `Projeto Final Grafos — LinkedIn Analyzer | [Seus nomes] | [Turma]`
3. Visibilidade: **Não listado** (o professor acessa pelo link, não aparece em buscas)
4. Após publicar, copie o link e confirme que ele abre em aba anônima antes de enviar

> ⚠️ Vídeo privado = nota zero conforme o enunciado. Use "Não listado", não "Privado".
