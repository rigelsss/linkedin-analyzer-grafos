package br.com.unipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class LinkedInAnalyzer {

    private final Grafo grafo;

    // O analyzer recebe o grafo pronto e usa essa mesma rede em todas as analises.
    public LinkedInAnalyzer(Grafo grafo) {
        this.grafo = grafo;
    }

    // Missao 2: Sugere pessoas de 2o grau (amigos de amigos ainda nao conectados),
    // ordenadas por quantidade de amigos em comum (decrescente).
    // O codigo utiliza um Map para contar os amigos em comum e depois ordena os resultados.
    public List<Map.Entry<String, Integer>> sugerirConexoes(String nome) {
        Vertice usuario = grafo.encontraVertice(nome)
                .orElseThrow(() -> new IllegalArgumentException("Usuario '" + nome + "' nao encontrado."));

        // Aqui garantimos que o nome informado realmente existe no grafo.
        // Esse conjunto representa os contatos diretos do usuario.
        Set<Vertice> conexoesDiretas = new HashSet<>(usuario.getAdjacencias());

        // O Map vai guardar: candidato -> quantidade de amigos em comum.
        Map<Vertice, Integer> amigosEmComum = new HashMap<>();

        // Percorremos cada amigo direto do usuario.
        for (Vertice amigo : conexoesDiretas) {
            // Depois olhamos os amigos desse amigo, que sao os candidatos de 2o grau.
            for (Vertice candidato : amigo.getAdjacencias()) {
                // So vale sugerir quem nao eh o proprio usuario e ainda nao esta conectado diretamente.
                if (!candidato.equals(usuario) && !conexoesDiretas.contains(candidato)) {
                    // merge incrementa a contagem sem precisar testar se o candidato ja estava no Map.
                    amigosEmComum.merge(candidato, 1, Integer::sum);
                }
            }
        }

        // Depois de contar tudo, ordenamos do maior numero de amigos em comum para o menor.
        // No final, convertemos de Vertice para String para devolver apenas nome e quantidade.
        return amigosEmComum.entrySet().stream()
                .sorted(Map.Entry.<Vertice, Integer>comparingByValue().reversed())
                .map(e -> Map.entry(e.getKey().getNome(), e.getValue()))
                .toList();
    }

    // Missao 3: Retorna o numero de passos (conexoes) entre duas pessoas via BFS.
    // Retorna -1 se nao houver caminho entre elas.
    public int grauDeSeparacao(String nomeOrigem, String nomeDestino) {
        Vertice vOrigem = grafo.encontraVertice(nomeOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Usuario '" + nomeOrigem + "' nao encontrado."));
        Vertice vDestino = grafo.encontraVertice(nomeDestino)
                .orElseThrow(() -> new IllegalArgumentException("Usuario '" + nomeDestino + "' nao encontrado."));

        // Se origem e destino forem a mesma pessoa, a distancia eh zero.
        if (vOrigem.equals(vDestino)) return 0;

        // A fila eh a estrutura central da BFS: quem entra primeiro eh processado primeiro.
        Queue<Vertice> fila = new LinkedList<>();

        // O Map de niveis guarda em quantos passos cada vertice foi alcancado.
        Map<Vertice, Integer> niveis = new HashMap<>();

        // A busca comeca na origem, que esta a 0 passos dela mesma.
        fila.add(vOrigem);
        niveis.put(vOrigem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            int nivelAtual = niveis.get(atual);

            // Na BFS, percorremos os vizinhos por camadas.
            for (Vertice vizinho : atual.getAdjacencias()) {
                // Se esse vizinho ainda nao apareceu no Map, ele ainda nao foi visitado.
                if (!niveis.containsKey(vizinho)) {
                    int proximoNivel = nivelAtual + 1;
                    niveis.put(vizinho, proximoNivel);

                    // A primeira vez que encontramos o destino na BFS ja garante o menor numero de passos.
                    if (vizinho.equals(vDestino)) return proximoNivel;

                    fila.add(vizinho);
                }
            }
        }

        // Se a fila terminou e o destino nao apareceu, nao existe conexao entre eles.
        return -1;
    }

    // Missao 4: Retorna a rota de maior afinidade (menor custo ponderado) via Dijkstra.
    // Se nao houver caminho, retorna custo -1 e caminho vazio.
    public ResultadoCaminho rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
        // Aqui a classe delega a logica do menor caminho ponderado para a classe Grafo.
        return grafo.dijkstra(nomeOrigem, nomeDestino);
    }

    // Missao 5: Mapeia todos os grupos isolados (componentes conexos) da rede via BFS.
    public List<List<String>> mapearGruposIsolados() {
        // Esse conjunto impede que o mesmo vertice seja processado mais de uma vez.
        Set<Vertice> visitados = new HashSet<>();

        // Cada elemento dessa lista final sera um grupo isolado encontrado na rede.
        List<List<String>> grupos = new ArrayList<>();

        for (Vertice v : grafo.getVertices()) {
            // Se o vertice ja foi visitado, ele ja pertence a algum grupo encontrado antes.
            if (visitados.contains(v)) continue;

            // Ao encontrar um vertice novo, comecamos uma BFS para montar um novo grupo.
            List<String> grupo = new ArrayList<>();
            Queue<Vertice> fila = new LinkedList<>();
            fila.add(v);
            visitados.add(v);

            while (!fila.isEmpty()) {
                Vertice atual = fila.poll();

                // Todo vertice alcancado nessa busca pertence ao mesmo componente conexo.
                grupo.add(atual.getNome());

                for (Vertice vizinho : atual.getAdjacencias()) {
                    if (!visitados.contains(vizinho)) {
                        visitados.add(vizinho);
                        fila.add(vizinho);
                    }
                }
            }

            // Quando a fila esvazia, terminamos de mapear um grupo isolado completo.
            grupos.add(grupo);
        }

        return grupos;
    }
}
