package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DadosAutor;
import br.com.alura.literalura.model.DadosLivrosWeb;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;
import br.com.alura.literalura.service.DadosRespostaApi;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Importação para @Transactional

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {
    private Scanner leitura = new Scanner(System.in);

    private final String url_base = "http://gutendex.com/books/";
    private ConverteDados conversor = new ConverteDados();
    private ConsumoApi consumoApi = new ConsumoApi();

    // Declarações para injeção de dependência
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    // Construtor para injeção de dependência (usado pelo Spring)
    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 9) {
            var menu = """
                   Escolha o número de sua opção:                    
                                        
                    1- Buscar livro pelo título
                    2- Listar livros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos em um determinado ano
                    5- Listar livros em um determinado idioma
                                    
                    9 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    buscarLivrosRegistradosBD();
                    break;
                case 3:
                    buscarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosNoAno();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 9:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // BUSCAR LIVROS POR TÍTULO - API
    @Transactional
    private void buscarLivroPorTitulo() {
        System.out.println("Digite o nome do livro que deseja buscar:");
        var nomeLivro = leitura.nextLine();
        var json = consumoApi.obterDados(url_base + "?search=" + nomeLivro.replace(" ", "+"));

        DadosRespostaApi dadosResposta = conversor.obterDados(json, DadosRespostaApi.class);

        if (dadosResposta != null && dadosResposta.results() != null && !dadosResposta.results().isEmpty()) {
            DadosLivrosWeb dadosLivroWeb = dadosResposta.results().get(0); // Pega o primeiro livro da lista

            // --- EXIBIR INFORMAÇÕES DO LIVRO ENCONTRADO PELA API ---
            System.out.println("\n--- LIVRO  ---");
            System.out.println("Título: " + dadosLivroWeb.titulo());
            if (dadosLivroWeb.autores() != null && !dadosLivroWeb.autores().isEmpty()) {
                System.out.print("Autores: ");
                for (int i = 0; i < dadosLivroWeb.autores().size(); i++) {
                    System.out.print(dadosLivroWeb.autores().get(i).nome());
                    if (i < dadosLivroWeb.autores().size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } else {
                System.out.println("Autores: Não informado");
            }
            System.out.println("Idiomas: " + String.join(", ", dadosLivroWeb.idiomas()));
            System.out.println("Número de Downloads: " + dadosLivroWeb.numeroDownloads());
            System.out.println("----------------------------------------\n");


            // --- PROSSEGUIR COM A PERSISTÊNCIA NO BANCO DE DADOS ---
            Optional<Livro> livroExistente = livroRepository.findByTitulo(dadosLivroWeb.titulo()); // Ajustado para findByTitulo

            if (livroExistente.isPresent()) {
                System.out.println("Livro já registrado no banco de dados.");
            } else { // Livro não existe, vamos criar e salvar
                Livro livro = new Livro(dadosLivroWeb);

                List<Autor> autoresParaAssociar = new ArrayList<>();
                for (DadosAutor dadosAutor : dadosLivroWeb.autores()) {
                    Optional<Autor> autorExistente = autorRepository.findByNome(dadosAutor.nome());
                    Autor autorGerenciado;

                    if (autorExistente.isPresent()) {
                        autorGerenciado = autorExistente.get(); // Já é gerenciado, ok
                    } else {
                        // Este é um novo autor. Salve-o explicitamente para que ele se torne gerenciado
                        // e tenha um ID antes de ser adicionado à lista do livro.
                        Autor novoAutor = new Autor(dadosAutor);
                        autorGerenciado = autorRepository.save(novoAutor); // Salva e retorna a instância gerenciada
                    }
                    autoresParaAssociar.add(autorGerenciado); // Adiciona a instância gerenciada (ou recém-salva e gerenciada)
                }
                livro.setAutores(autoresParaAssociar);

                livroRepository.save(livro);
                System.out.println("Livro salvo com sucesso no banco de dados!");
            }


        } else {
            System.out.println("Nenhum livro encontrado com o título: " + nomeLivro);
        }
        System.out.println("\n-------------------------");
    }

    // BUSCAR LIVROS DO BANCO - POSTGRES
    private void buscarLivrosRegistradosBD() {
        List<Livro> livros = livroRepository.findAll();

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado no banco de dados ainda.");
        } else {
            System.out.println("\n--- LIVROS REGISTRADOS NO BANCO DE DADOS ---");
            for (Livro livro : livros) {
                System.out.println("\n--- LIVRO ---");
                System.out.println("Título: " + livro.getTitulo());

                String nomesAutores = livro.getAutores().stream()
                        .map(Autor::getNome)
                        .collect(Collectors.joining(", "));
                System.out.println("Autores: " + nomesAutores);

                String idiomas = String.join(", ", livro.getIdiomas());
                System.out.println("Idiomas: " + idiomas);

                System.out.println("Número de Downloads: " + livro.getNumeroDownloads());
            }
            System.out.println("\n-------------------------------------------");
        }
    }

    private void buscarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado no banco de dados ainda.");
        } else {
            System.out.println("\n--- AUTORES REGISTRADOS NO BANCO DE DADOS ---");
            for (Autor autor : autores) {
                System.out.println("\nAutor: " + autor.getNome());
                System.out.println("Ano de Nascimento: " + (autor.getAnoNascimento() != null ? autor.getAnoNascimento() : "N/A"));
                System.out.println("Ano de Falecimento: " + (autor.getAnoFalecimento() != null ? autor.getAnoFalecimento() : "N/A"));

                // --- NOVO: EXIBIR LIVROS DO AUTOR ---
                if (autor.getLivros() != null && !autor.getLivros().isEmpty()) {
                    String titulosLivros = autor.getLivros().stream()
                            .map(livro -> "[" + livro.getTitulo() + "]")
                            .collect(Collectors.joining(", "));
                    System.out.println("Livros: " + titulosLivros);
                } else {
                    System.out.println("Livros: Nenhum livro associado.");
                }
                // --- FIM NOVO ---

                System.out.println("------------------------------------------");
            }
            System.out.println("\n-------------------------------------------");
        }
    }

    private void listarAutoresVivosNoAno() {
        System.out.println("Digite o ano para verificar autores vivos:");
        Integer ano = null; // Inicializa como null

        // Loop para garantir que o usuário insira um ano válido
        while (ano == null) {
            try {
                System.out.print("Ano: ");
                ano = leitura.nextInt();
                leitura.nextLine(); // Consome a nova linha
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, digite um ano válido (somente números inteiros).");
                leitura.nextLine(); // Consome a linha inválida para evitar loop infinito
                ano = null; // Garante que o loop continue
            }
        }

        List<Autor> autoresVivos = autorRepository.findAutoresVivosNoAno(ano);

        if (autoresVivos.isEmpty()) {
            System.out.println("Nenhum autor vivo encontrado no ano " + ano + ".");
        } else {
            System.out.println("\n--- AUTORES VIVOS NO ANO " + ano + " ---");
            for (Autor autor : autoresVivos) {
                System.out.println("\nAutor: " + autor.getNome());
                System.out.println("Ano de Nascimento: " + (autor.getAnoNascimento() != null ? autor.getAnoNascimento() : "N/A"));
                System.out.println("Ano de Falecimento: " + (autor.getAnoFalecimento() != null ? autor.getAnoFalecimento() : "N/A"));

                if (autor.getLivros() != null && !autor.getLivros().isEmpty()) {
                    String titulosLivros = autor.getLivros().stream()
                            .map(livro -> "[" + livro.getTitulo() + "]")
                            .collect(Collectors.joining(", "));
                    System.out.println("Livros: " + titulosLivros);
                } else {
                    System.out.println("Livros: Nenhum livro associado.");
                }
                System.out.println("------------------------------------------");
            }
            System.out.println("\n-------------------------------------------");
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("""
            Selecione o idioma:
            es - Espanhol
            en - Inglês
            fr - Francês
            pt - Português
            """);
        System.out.print("Digite a sigla do idioma (ex: en): ");
        var idioma = leitura.nextLine().trim().toLowerCase(); // Garante que a entrada é tratada para minúsculas e sem espaços extras

        List<Livro> livrosNoIdioma = livroRepository.findByIdiomasContaining(idioma);

        if (livrosNoIdioma.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o idioma '" + idioma + "' no banco de dados.");
        } else {
            System.out.println("\n--- LIVROS NO IDIOMA '" + idioma.toUpperCase() + "' ---");
            for (Livro livro : livrosNoIdioma) {
                System.out.println("\n--- LIVRO ---");
                System.out.println("Título: " + livro.getTitulo());

                String nomesAutores = livro.getAutores().stream()
                        .map(Autor::getNome)
                        .collect(Collectors.joining(", "));
                System.out.println("Autores: " + nomesAutores);

                String idiomasLivro = String.join(", ", livro.getIdiomas());
                System.out.println("Idiomas: " + idiomasLivro);

                System.out.println("Número de Downloads: " + livro.getNumeroDownloads());
                System.out.println("-------------------------------------------");
            }
            System.out.println("\nTotal de livros em " + idioma.toUpperCase() + ": " + livrosNoIdioma.size());
            System.out.println("-------------------------------------------");
        }
    }

}
