package gov.antt.sifama.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Tro implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    String palavraChave;
    String observacao;
    String prazo;
    String severidade;

    @OneToMany(mappedBy = "tro", fetch = FetchType.EAGER)
    List<Local> locais = new ArrayList<>();


    public Tro(Integer id, String descricao){
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getSeveridade() {
        return severidade;
    }

    public void setSeveridade(String severidade) {
        this.severidade = severidade;
    }
}


