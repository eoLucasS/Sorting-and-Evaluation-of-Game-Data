import java.io.*;
import java.util.*;

/**
 * Classe que representa um jogo com nome, categoria e avaliação.
 */
class Jogo {
    String nome;
    String categoria;
    double avaliacao;

    /**
     * Constrói uma instância de Jogo.
     *
     * @param nome      Nome do jogo.
     * @param categoria Categoria do jogo.
     * @param avaliacao Avaliação do jogo no intervalo de 0 a 5.
     */
    public Jogo(String nome, String categoria, double avaliacao) {
        this.nome = nome;
        this.categoria = categoria;
        this.avaliacao = avaliacao;
    }
}

/**
 * Interface para algoritmos de ordenação de listas de jogos.
 */
interface Ordenavel {
    /**
     * Ordena a lista de jogos de acordo com o comparador especificado.
     *
     * @param jogos      Lista de jogos a ser ordenada.
     * @param comparador Critério de comparação para ordenação.
     */
    void ordenar(List<Jogo> jogos, Comparator<Jogo> comparador);
}

/**
 * Implementação do algoritmo de ordenação por seleção.
 */
class OrdenadorPorSelecao implements Ordenavel {
    @Override
    public void ordenar(List<Jogo> jogos, Comparator<Jogo> comparador) {
        int n = jogos.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (comparador.compare(jogos.get(j), jogos.get(minIdx)) < 0) {
                    minIdx = j;
                }
            }
            Collections.swap(jogos, i, minIdx);
        }
    }
}

/**
 * Implementação do algoritmo de ordenação por bolha.
 */
class OrdenadorPorBolha implements Ordenavel {
    @Override
    public void ordenar(List<Jogo> jogos, Comparator<Jogo> comparador) {
        int n = jogos.size();
        boolean trocou;
        do {
            trocou = false;
            for (int i = 0; i < n - 1; i++) {
                if (comparador.compare(jogos.get(i + 1), jogos.get(i)) < 0) {
                    Collections.swap(jogos, i, i + 1);
                    trocou = true;
                }
            }
        } while (trocou);
    }
}

/**
 * Gerenciador principal que carrega, ordena e salva jogos de um arquivo CSV.
 */
public class GerenciadorDeJogos {
    /**
     * Carrega jogos de um arquivo CSV.
     *
     * @param path Caminho do arquivo a ser lido.
     * @return Lista de jogos carregados.
     * @throws IOException Se ocorrer um erro ao ler o arquivo.
     */
    private static List<Jogo> carregarJogos(String path) throws IOException {
        List<Jogo> jogos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // Ignora o cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                double avaliacao = Double.parseDouble(dados[2].replace(',', '.'));
                jogos.add(new Jogo(dados[0], dados[1], avaliacao));
            }
        }
        return jogos;
    }    

    /**
     * Salva uma lista de jogos em um arquivo CSV dentro de um diretório específico.
     *
     * @param jogos    Lista de jogos a serem salvos.
     * @param dirPath  Caminho do diretório onde o arquivo será criado.
     * @param fileName Nome do arquivo a ser criado.
     * @throws IOException Se ocorrer um erro ao escrever no arquivo.
     */
    private static void salvarJogos(List<Jogo> jogos, String dirPath, String fileName) throws IOException {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory, fileName)))) {
            bw.write("Nome,Categoria,Avaliacao\n");
            for (Jogo jogo : jogos) {
                // Aqui é garantido que a saída use o ponto como separador decimal.
                String formattedAvaliacao = String.format(Locale.ROOT, "%.1f", jogo.avaliacao);
                bw.write(jogo.nome + "," + jogo.categoria + "," + formattedAvaliacao + "\n");
            }
        }
    }

    /**
     * Ponto de entrada do programa. Gerencia interações do usuário através de um menu.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     * @throws IOException Se ocorrer um erro de entrada/saída.
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        List<Jogo> jogos = null;

        System.out.println("Author:");
        System.out.println("Lucas Lopes da Silva - RA: 822154790");

        while (true) {
            System.out.println("[1] Carregar jogos");
            System.out.println("[2] Ordenar por categoria");
            System.out.println("[3] Ordenar por avaliação");
            System.out.println("[4] Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    jogos = carregarJogos("JogosDesordenados.csv");
                    System.out.println("Arquivo CSV lido com sucesso!");
                    break;
                case 2:
                    if (jogos == null) {
                        System.out.println("Carregue os jogos primeiro!");
                    } else {
                        Ordenavel ordenadorCategoria = new OrdenadorPorSelecao();
                        ordenadorCategoria.ordenar(jogos, Comparator.comparing(j -> j.categoria));
                        salvarJogos(jogos, "Ordenado por Categoria", "JogosOrdenadosPorCategoria.csv");
                        System.out.println("Jogos ordenados por categoria e salvos.");
                    }
                    break;
                case 3:
                    if (jogos == null) {
                        System.out.println("Carregue os jogos primeiro!");
                    } else {
                        Ordenavel ordenadorAvaliacao = new OrdenadorPorBolha();
                        ordenadorAvaliacao.ordenar(jogos, (j1, j2) -> Double.compare(j2.avaliacao, j1.avaliacao));
                        salvarJogos(jogos, "Ordenado por Avaliação", "JogosOrdenadosPorAvaliacao.csv");
                        System.out.println("Jogos ordenados por avaliação e salvos.");
                    }
                    break;
                case 4:
                    System.out.println("Encerrando o programa.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }
}