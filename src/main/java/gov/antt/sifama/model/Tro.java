package gov.antt.sifama.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Tro implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String palavraChave;
    String observacao;
    String prazo;

    @OneToMany(mappedBy = "tro", fetch = FetchType.EAGER)
    List<Local> locais = new ArrayList<>();


    public Tro(Long id, String descricao){
        this.id = id;
        this.palavraChave = descricao;
    }
    public Tro(){

    }

    public String getDescricao() {
        return palavraChave;
    }

    public void setDescricao(String descricao) {
        this.palavraChave = descricao;
    }

    public List<Local> getLocais() {
        return locais;
    }

    public void setLocais(List<Local> locais) {
        this.locais = locais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPalavraChave() {
        return palavraChave;
    }

    public void setPalavraChave(String palavraChave) {
        this.palavraChave = palavraChave;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }
}


