package br.com.alura.literalura.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "livros") // Nome da tabela no banco
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Garante que o título seja único
    private String titulo;

    // Relacionamento Muitos-para-Muitos com Autor
    @ManyToMany(fetch = FetchType.EAGER) // Remova cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    @JoinTable(name = "livros_autores",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id"))
    private List<Autor> autores;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "livro_idiomas", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "idioma")
    private List<String> idiomas;

    private Integer numeroDownloads;

    // Construtor padrão exigido pelo JPA
    public Livro() {}


    public Livro(DadosLivrosWeb dadosLivroWeb) {
        this.titulo = dadosLivroWeb.titulo();
        this.autores = dadosLivroWeb.autores().stream()
                .map(Autor::new) // <--- Use Autor::new aqui
                .collect(Collectors.toList());
        this.idiomas = dadosLivroWeb.idiomas();
        this.numeroDownloads = dadosLivroWeb.numeroDownloads();
    }

    // Getters e Setters (necessários para JPA)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public List<Autor> getAutores() { return autores; }
    public void setAutores(List<Autor> autores) { this.autores = autores; }
    public List<String> getIdiomas() { return idiomas; }
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
    public Integer getNumeroDownloads() { return numeroDownloads; }
    public void setNumeroDownloads(Integer numeroDownloads) { this.numeroDownloads = numeroDownloads; }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autores=" + (autores != null ? autores.stream().map(Autor::getNome).collect(Collectors.joining(", ")) : "N/A") +
                ", idiomas=" + idiomas +
                ", numeroDownloads=" + numeroDownloads +
                '}';
    }
}
