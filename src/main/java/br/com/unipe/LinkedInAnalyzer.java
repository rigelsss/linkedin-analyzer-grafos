package br.com.unipe;

import java.util.*;

public class LinkedInAnalyzer {

    private final Grafo grafo;

    public LinkedInAnalyzer(Grafo grafo) {
        this.grafo = grafo;
    }

    // Missão 2: Sugere pessoas de 2º grau (amigos de amigos ainda não conectados),
    // ordenadas por quantidade de amigos em comum (decrescente).
    public List<Map.Entry<String, Integer>> sugerirConexoes(String nome) {
        Vertice usuario = grafo.encontraVertice(nome)
                .orElseThrow(() -> new IllegalArgumentException("Usuário '" + nome + "' não encontrado."));

        Set<Vertice> conexoesDiretas = new HashSet<>(usuario.getAdjacencias());
        Map<Vertice, Integer> amigosEmComum = new HashMap<>();

        for (Vertice amigo : conexoesDiretas) {
            for (Vertice candidato : amigo.getAdjacencias()) {
                if (!candidato.equals(usuario) && !conexoesDiretas.contains(candidato)) {
                    amigosEmComum.merge(candidato, 1, Integer::sum);
                }
            }
        }

        return amigosEmComum.entrySet().stream()
                .sorted(Map.Entry.<Vertice, Integer>comparingByValue().reversed())
                .map(e -> Map.entry(e.getKey().getNome(), e.getValue()))
                .toList();
    }

    // Missão 3: Retorna o número de passos (conexões) entre duas pessoas via BFS.
    // Retorna -1 se não houver caminho entre elas.
    public int grauDeSeparacao(String nomeOrigem, String nomeDestino) {
        Vertice vOrigem = grafo.encontraVertice(nomeOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Usuário '" + nomeOrigem + "' não encontrado."));
        Vertice vDestino = grafo.encontraVertice(nomeDestino)
                .orElseThrow(() -> new IllegalArgumentException("Usuário '" + nomeDestino + "' não encontrado."));

        if (vOrigem.equals(vDestino)) return 0;

        Queue<Vertice> fila = new LinkedList<>();
        Map<Vertice, Integer> niveis = new HashMap<>();

        fila.add(vOrigem);
        niveis.put(vOrigem, 0);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            int nivelAtual = niveis.get(atual);

            for (Vertice vizinho : atual.getAdjacencias()) {
                if (!niveis.containsKey(vizinho)) {
                    int proximoNivel = nivelAtual + 1;
                    niveis.put(vizinho, proximoNivel);
                    if (vizinho.equals(vDestino)) return proximoNivel;
                    fila.add(vizinho);
                }
            }
        }

        return -1;
    }

    // Missão 4: Retorna a rota de maior afinidade (menor custo ponderado) via Dijkstra.
    // Se não houver caminho, retorna custo -1 e caminho vazio.
    public ResultadoCaminho rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
        return grafo.dijkstra(nomeOrigem, nomeDestino);
    }

    // Missão 5: Mapeia todos os grupos isolados (componentes conexos) da rede via BFS.
    public List<List<String>> mapearGruposIsolados() {
        Set<Vertice> visitados = new HashSet<>();
        List<List<String>> grupos = new ArrayList<>();

        for (Vertice v : grafo.getVertices()) {
            if (visitados.contains(v)) continue;

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
    }
}
