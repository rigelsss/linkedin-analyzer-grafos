package br.com.unipe;

import java.util.List;
import java.util.Map;

public class LinkedInApp {

    public static void main(String[] args) {
        Grafo grafo = new Grafo(false, true);

        grafo.adicionaVertices("Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda",
                "Gabriel", "Hugo", "Igor", "Juliana");

        // Rede principal
        grafo.addAresta("Ana", "Bruno", 1);
        grafo.addAresta("Ana", "Carlos", 2);
        grafo.addAresta("Ana", "Daniela", 8);
        grafo.addAresta("Bruno", "Eduardo", 1);
        grafo.addAresta("Carlos", "Eduardo", 1);
        grafo.addAresta("Daniela", "Fernanda", 5);
        grafo.addAresta("Eduardo", "Fernanda", 1);

        // Grupos isolados
        grafo.addAresta("Gabriel", "Hugo", 1);
        grafo.addAresta("Igor", "Juliana", 1);

        LinkedInAnalyzer analyzer = new LinkedInAnalyzer(grafo);

        // --- Missão 2: Sugestão de Conexões ---
        System.out.println("=== Missão 2: Sugestão de Conexões para Ana ===");
        List<Map.Entry<String, Integer>> sugestoes = analyzer.sugerirConexoes("Ana");
        if (sugestoes.isEmpty()) {
            System.out.println("  Nenhuma sugestão encontrada.");
        } else {
            sugestoes.forEach(s ->
                System.out.println("  " + s.getKey() + " — " + s.getValue() + " amigo(s) em comum"));
        }

        // --- Missão 3: Grau de Separação ---
        System.out.println("\n=== Missão 3: Grau de Separação ===");
        imprimeSeparacao(analyzer, "Ana", "Fernanda");
        imprimeSeparacao(analyzer, "Ana", "Bruno");
        imprimeSeparacao(analyzer, "Ana", "Gabriel");
        imprimeSeparacao(analyzer, "Gabriel", "Hugo");

        // --- Missão 4: Rota de Maior Afinidade ---
        System.out.println("\n=== Missão 4: Rota de Maior Afinidade ===");
        imprimeRota(analyzer, "Ana", "Fernanda");
        imprimeRota(analyzer, "Ana", "Carlos");
        imprimeRota(analyzer, "Ana", "Igor");

        // --- Missão 5: Grupos Isolados ---
        System.out.println("\n=== Missão 5: Grupos Isolados (Sub-redes) ===");
        List<List<String>> grupos = analyzer.mapearGruposIsolados();
        for (int i = 0; i < grupos.size(); i++) {
            System.out.println("  Grupo " + (i + 1) + ": " + grupos.get(i));
        }
    }

    private static void imprimeSeparacao(LinkedInAnalyzer analyzer, String origem, String destino) {
        int grau = analyzer.grauDeSeparacao(origem, destino);
        String resultado = grau == -1 ? "sem conexão" : grau + " passo(s)";
        System.out.println("  " + origem + " → " + destino + ": " + resultado);
    }

    private static void imprimeRota(LinkedInAnalyzer analyzer, String origem, String destino) {
        ResultadoCaminho resultado = analyzer.rotaDeMaiorAfinidade(origem, destino);
        if (!resultado.encontrou()) {
            System.out.println("  " + origem + " → " + destino + ": sem caminho");
        } else {
            System.out.println("  Caminho: " + String.join(" → ", resultado.caminho()));
            System.out.println("  Custo total: " + resultado.custo());
        }
    }
}
