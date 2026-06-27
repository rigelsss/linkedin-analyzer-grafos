# Explicação Completa do Projeto — LinkedIn Analyzer

## Visão Geral

O projeto modela uma rede social profissional como um **grafo não-direcionado e ponderado**.
Cada pessoa é um vértice; cada conexão entre duas pessoas é uma aresta com um peso que representa a afinidade (peso 1 = muita afinidade, peso 5 ou mais = pouca afinidade).

A partir dessa estrutura, a classe `LinkedInAnalyzer` executa quatro análises usando algoritmos clássicos de grafos.

---

## As Classes

### `Vertice.java`

Representa uma pessoa na rede.

```java
private String nome;
private int grau;         // total de conexões (não-dirigido)
private int inDegree;     // usado apenas em dígrafos
private int outDegree;    // usado apenas em dígrafos
private List<Vertice> adjacencias; // vizinhos para onde este vértice aponta (out)
private List<Vertice> adjacentes;  // vizinhos que apontam para este vértice (in)
```

- `adjacencias` é a lista de "amigos diretos" de uma pessoa — é o que usamos para percorrer o grafo.
- Para grafos não-dirigidos, ambas as listas são populadas em ambos os lados de cada aresta.
- As anotações `@Getter` e `@Setter` do Lombok geram automaticamente todos os métodos `get` e `set`.

---

### `Aresta.java`

Representa a conexão entre duas pessoas.

```java
private String nome;
private Vertice verticeOrigem;
private Vertice verticeDestino;
private Integer peso;
```

- O `peso` é o valor de afinidade da conexão.
- Para grafos não-dirigidos, o par origem/destino é apenas a ordem em que a aresta foi inserida — a conexão vale nas duas direções.

---

### `Grafo.java`

É o núcleo da estrutura de dados. Mantém as listas de vértices e arestas e oferece operações sobre elas.

#### Construtor

```java
public Grafo(boolean eDirigido, boolean ePonderado)
```

Para o LinkedIn Analyzer usamos `new Grafo(false, true)` — não-dirigido e ponderado.

#### `adicionaVertices(String... nomes)`

Recebe um número variável de nomes e cria um `Vertice` para cada um, adicionando à lista interna e incrementando `ordem` (total de vértices).

#### `addAresta(String v1, String v2, int peso)`

Chama `criaAresta` internamente, que:
1. Localiza os dois vértices pelo nome.
2. Verifica se o grafo continua não-dirigido (sem self-loops ou arestas duplicadas).
3. Aumenta o grau dos dois vértices.
4. Chama `resolveAdjacencias`: para um grafo não-dirigido, adiciona cada vértice na lista `adjacencias` **e** `adjacentes` do outro — garantindo que a travessia funcione nos dois sentidos.
5. Incrementa `tamanho` (total de arestas) e cria o objeto `Aresta`.

#### `encontraVertice(String nome)`

Busca linear na lista de vértices, comparando nomes ignorando maiúsculas/minúsculas. Retorna `Optional<Vertice>`.

#### `getVertices()`

Retorna uma visão não-modificável da lista de vértices — adicionado para que `LinkedInAnalyzer` possa iterar sobre todos os vértices sem expor a lista interna.

#### `dfsIterativo(String origem, String destino)`

Busca em profundidade (DFS) usando uma pilha (`Stack`).
Útil para percorrer o grafo sem se perder em ciclos.
Visita vizinhos em ordem alfabética.

#### `greedySearch(String origem, String destino)`

Busca gulosa ponderada: a cada passo escolhe a aresta de **menor peso** entre os vizinhos ainda não visitados. Não garante o caminho ótimo global — apenas toma a melhor decisão local.

---

#### `dijkstra(String nomeOrigem, String nomeDestino)` ← método adicionado

Este é o algoritmo central do projeto. Encontra o **caminho de menor custo acumulado** (maior afinidade) entre dois vértices em um grafo ponderado.

**Por que Dijkstra e não BFS ou Greedy?**
- BFS encontra o menor número de passos, mas ignora os pesos.
- Greedy escolhe a melhor aresta a cada passo localmente, mas pode errar no global.
- Dijkstra considera o custo total acumulado desde a origem — garante o resultado ótimo.

**Funcionamento passo a passo:**

```java
record Entrada(Vertice vertice, int custo) {}
```
Usamos um `record` local (Java 16+) para emparelhar vértice + custo acumulado na fila de prioridade.

```java
Map<Vertice, Integer> distancias = new HashMap<>();
```
Guarda a menor distância conhecida da origem até cada vértice. Inicializada com `Integer.MAX_VALUE` (infinito) para todos, exceto a origem que começa em 0.

```java
Map<Vertice, Vertice> anteriores = new HashMap<>();
```
Rastreia o vértice anterior no caminho ótimo — usado para reconstruir a rota ao final.

```java
Set<Vertice> visitados = new HashSet<>();
PriorityQueue<Entrada> fila = new PriorityQueue<>(Comparator.comparingInt(Entrada::custo));
```
A fila de prioridade sempre retira o vértice com menor custo acumulado. O conjunto `visitados` garante que cada vértice é processado uma única vez.

**Loop principal:**
```java
while (!fila.isEmpty()) {
    Entrada entrada = fila.poll();       // pega o de menor custo
    Vertice atual = entrada.vertice();

    if (visitados.contains(atual)) continue;  // já processado, ignora
    visitados.add(atual);

    if (atual.equals(vDestino)) break;   // chegou no destino, para

    for (Vertice vizinho : atual.getAdjacencias()) {
        int peso = obtemArestasParaVizinho(atual, vizinho)...min();
        int novaDistancia = distancias.get(atual) + peso;

        if (novaDistancia < distancias.get(vizinho)) {   // encontrou caminho melhor
            distancias.put(vizinho, novaDistancia);
            anteriores.put(vizinho, atual);
            fila.add(new Entrada(vizinho, novaDistancia));
        }
    }
}
```

**Reconstrução do caminho:**
Partindo do destino, segue o mapa `anteriores` de volta até a origem, inserindo cada vértice no início da lista.

**Retorno:**
```java
return new ResultadoCaminho(caminho, distancias.get(vDestino));
```
Se não houver caminho, retorna `custo = -1` e lista vazia.

**Exemplo do cenário de testes:**
- `Ana → Daniela → Fernanda`: custo = 8 + 5 = **13**
- `Ana → Bruno → Eduardo → Fernanda`: custo = 1 + 1 + 1 = **3** ← Dijkstra escolhe este

---

### `ResultadoCaminho.java`

```java
public record ResultadoCaminho(List<String> caminho, int custo) {
    public boolean encontrou() { return custo != -1; }
}
```

`record` é um tipo Java (desde Java 16) que cria automaticamente construtor, getters, `equals`, `hashCode` e `toString`. É ideal para objetos de dados imutáveis.
- `caminho()` — lista ordenada de nomes da rota
- `custo()` — soma dos pesos das arestas percorridas
- `encontrou()` — atalho para saber se existe caminho

---

### `LinkedInAnalyzer.java`

Recebe o `Grafo` no construtor e implementa as quatro missões.

```java
public LinkedInAnalyzer(Grafo grafo) {
    this.grafo = grafo;
}
```

#### Missão 2 — `sugerirConexoes(String nome)`

**Objetivo:** Encontrar "amigos de amigos" que o usuário ainda não conhece, ordenados por quantidade de amigos em comum.

**Lógica:**
```java
Set<Vertice> conexoesDiretas = new HashSet<>(usuario.getAdjacencias());
```
Coleta todos os contatos diretos em um `Set` (busca O(1)).

```java
for (Vertice amigo : conexoesDiretas) {
    for (Vertice candidato : amigo.getAdjacencias()) {
        if (!candidato.equals(usuario) && !conexoesDiretas.contains(candidato)) {
            amigosEmComum.merge(candidato, 1, Integer::sum);
        }
    }
}
```
Para cada amigo direto, percorre os amigos dele. Se o candidato não é o próprio usuário e não é um contato direto, contabiliza mais um amigo em comum usando `Map.merge` (incremento seguro).

```java
return amigosEmComum.entrySet().stream()
    .sorted(Map.Entry.<Vertice, Integer>comparingByValue().reversed())
    .map(e -> Map.entry(e.getKey().getNome(), e.getValue()))
    .toList();
```
Ordena por valor (amigos em comum) de forma decrescente e converte para pares `(nome, contagem)`.

**Resultado para Ana:**
- Eduardo aparece via Bruno e via Carlos → 2 amigos em comum
- Fernanda aparece via Daniela → 1 amigo em comum

---

#### Missão 3 — `grauDeSeparacao(String nomeOrigem, String nomeDestino)`

**Objetivo:** Encontrar o menor número de "passos" entre duas pessoas. Usa **BFS** (Busca em Largura).

**Por que BFS e não DFS?**
DFS vai fundo em um caminho e pode encontrar rotas longas. BFS expande nível a nível, garantindo que a primeira vez que o destino é encontrado é pelo caminho com menos passos.

**Lógica:**
```java
Queue<Vertice> fila = new LinkedList<>();
Map<Vertice, Integer> niveis = new HashMap<>();

fila.add(vOrigem);
niveis.put(vOrigem, 0);  // origem está no nível 0

while (!fila.isEmpty()) {
    Vertice atual = fila.poll();
    int nivelAtual = niveis.get(atual);

    for (Vertice vizinho : atual.getAdjacencias()) {
        if (!niveis.containsKey(vizinho)) {  // ainda não visitado
            int proximoNivel = nivelAtual + 1;
            niveis.put(vizinho, proximoNivel);
            if (vizinho.equals(vDestino)) return proximoNivel;  // achou!
            fila.add(vizinho);
        }
    }
}
return -1;  // destino inacessível
```

O `Map<Vertice, Integer> niveis` serve como visitados e ao mesmo tempo guarda a distância em número de passos.

**Resultados:**
- Ana → Fernanda: 2 (Ana-Daniela-Fernanda ou Ana-Bruno-Eduardo-Fernanda — BFS retorna o menor)
- Ana → Gabriel: -1 (grupos isolados)

---

#### Missão 4 — `rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino)`

```java
public ResultadoCaminho rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
    return grafo.dijkstra(nomeOrigem, nomeDestino);
}
```

Delega diretamente ao `dijkstra` implementado em `Grafo`. Retorna o `ResultadoCaminho` com caminho e custo.

---

#### Missão 5 — `mapearGruposIsolados()`

**Objetivo:** Encontrar todos os componentes conexos do grafo — grupos de pessoas que se conectam entre si mas não alcançam outros grupos.

**Lógica:**
```java
Set<Vertice> visitados = new HashSet<>();
List<List<String>> grupos = new ArrayList<>();

for (Vertice v : grafo.getVertices()) {
    if (visitados.contains(v)) continue;  // já pertence a um grupo

    // BFS a partir de v para mapear todo o seu componente
    List<String> grupo = new ArrayList<>();
    Queue<Vertice> fila = new LinkedList<>();
    fila.add(v);
    visitados.add(v);

    while (!fila.isEmpty()) {
        Vertice atual = fila.poll();
        grupo.add(atual.getNome());
        for (Vertice vizinho : atual.getAdjacencias()) {
            if (!visitados.contains(vizinho)) {
                visitados.add(vizinho);
                fila.add(vizinho);
            }
        }
    }
    grupos.add(grupo);
}
return grupos;
```

A cada vértice ainda não visitado, inicia um novo BFS que alcança exatamente todos os membros do mesmo componente. Ao final, `grupos` contém um subconjunto por componente.

**Resultado:**
- Grupo 1: Ana, Bruno, Carlos, Daniela, Eduardo, Fernanda
- Grupo 2: Gabriel, Hugo
- Grupo 3: Igor, Juliana

---

### `LinkedInApp.java`

Classe com o `main` que monta o cenário de testes do enunciado e chama cada missão do `LinkedInAnalyzer`, exibindo os resultados formatados.

---

## Cenário de Testes — Diagrama da Rede

```
        [Gabriel]---1---[Hugo]        [Igor]---1---[Juliana]
        (isolado)                     (isolado)

   Ana ---1--- Bruno
    |  \         \
    2   8         1
    |    \         \
  Carlos  Daniela  Eduardo
    \        \      /
     1        5    1
      \        \  /
      Eduardo  Fernanda
```

*(Eduardo conecta a Ana via Carlos e Bruno; Fernanda conecta via Daniela e Eduardo)*

---

## Resumo dos Algoritmos

| Missão | Algoritmo | Complexidade | Garante ótimo? |
|--------|-----------|-------------|----------------|
| Sugestão de conexões | Travessia de adjacências | O(V + E) | — |
| Grau de separação | BFS | O(V + E) | Sim (menor nº de passos) |
| Rota de maior afinidade | Dijkstra | O((V + E) log V) | Sim (menor custo ponderado) |
| Grupos isolados | BFS por componente | O(V + E) | Sim (todos os componentes) |

V = número de vértices, E = número de arestas.
