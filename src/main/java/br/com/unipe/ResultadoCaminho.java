package br.com.unipe;

import java.util.List;

public record ResultadoCaminho(List<String> caminho, int custo) {

    public boolean encontrou() {
        return custo != -1;
    }
}
