package br.com.unipe;

import java.util.List;
import java.util.Map;

public class LinkedInApp {

    public static void main(String[] args) {
        // Criamos um grafo nao dirigido e ponderado
        Grafo grafo = new Grafo(false, true);

        // Cada nome abaixo representa um vertice da rede social.
        grafo.adicionaVertices("Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda",
                "Gabriel", "Hugo", "Igor", "Juliana");

        // Rede principal: esse eh o maior grupo conectado do exemplo.
        grafo.addAresta("Ana", "Bruno", 1);
        grafo.addAresta("Ana", "Carlos", 2);
        grafo.addAresta("Ana", "Daniela", 8);
        grafo.addAresta("Bruno", "Eduardo", 1);
        grafo.addAresta("Carlos", "Eduardo", 1);
        grafo.addAresta("Daniela", "Fernanda", 5);
        grafo.addAresta("Eduardo", "Fernanda", 1);

        // Grupos isolados: servem para demonstrar componentes conexos separados.
        grafo.addAresta("Gabriel", "Hugo", 1);
        grafo.addAresta("Igor", "Juliana", 1);

        // A partir daqui, o analyzer passa a fazer as consultas sobre a rede montada.
        LinkedInAnalyzer analyzer = new LinkedInAnalyzer(grafo);

        // --- Missao 2: Sugestao de Conexoes ---
        System.out.println("=== Missao 2: Sugestao de Conexoes para Ana ===");
        // Pedimos sugestoes para Ana, procurando conexoes de 2o grau.
        List<Map.Entry<String, Integer>> sugestoes = analyzer.sugerirConexoes("Ana");
        if (sugestoes.isEmpty()) {
            System.out.println("  Nenhuma sugestao encontrada.");
        } else {
            // Cada item traz: nome sugerido + quantidade de amigos em comum.
            sugestoes.forEach(s ->
                System.out.println("  " + s.getKey() + " - " + s.getValue() + " amigo(s) em comum"));
        }

        // --- Missao 3: Grau de Separacao ---
        System.out.println("\n=== Missao 3: Grau de Separacao ===");
        // Aqui medimos a distancia em numero de passos entre dois perfis.
        imprimeSeparacao(analyzer, "Ana", "Fernanda");
        imprimeSeparacao(analyzer, "Ana", "Bruno");
        imprimeSeparacao(analyzer, "Ana", "Gabriel");
        imprimeSeparacao(analyzer, "Gabriel", "Hugo");

        // --- Missao 4: Rota de Maior Afinidade ---
        System.out.println("\n=== Missao 4: Rota de Maior Afinidade ===");
        // Aqui ja nao importa a quantidade de passos, e sim o menor custo total do caminho.
        imprimeRota(analyzer, "Ana", "Fernanda");
        imprimeRota(analyzer, "Ana", "Carlos");
        imprimeRota(analyzer, "Ana", "Igor");

        // --- Missao 5: Grupos Isolados ---
        System.out.println("\n=== Missao 5: Grupos Isolados (Sub-redes) ===");
        // Esse metodo varre a rede inteira e devolve cada componente conexo encontrado.
        List<List<String>> grupos = analyzer.mapearGruposIsolados();
        for (int i = 0; i < grupos.size(); i++) {
            System.out.println("  Grupo " + (i + 1) + ": " + grupos.get(i));
        }
    }

    private static void imprimeSeparacao(LinkedInAnalyzer analyzer, String origem, String destino) {
        // O retorno eh um inteiro: 0 para a mesma pessoa, 1 para conexao direta e assim por diante.
        int grau = analyzer.grauDeSeparacao(origem, destino);
        String resultado = grau == -1 ? "sem conexao" : grau + " passo(s)";
        System.out.println("  " + origem + " -> " + destino + ": " + resultado);
    }

    private static void imprimeRota(LinkedInAnalyzer analyzer, String origem, String destino) {
        // ResultadoCaminho guarda duas informacoes: a sequencia do caminho e o custo total.
        ResultadoCaminho resultado = analyzer.rotaDeMaiorAfinidade(origem, destino);
        if (!resultado.encontrou()) {
            System.out.println("  " + origem + " -> " + destino + ": sem caminho");
        } else {
            System.out.println("  Caminho: " + String.join(" -> ", resultado.caminho()));
            System.out.println("  Custo total: " + resultado.custo());
        }
    }
}
