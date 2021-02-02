package gov.antt.sifama.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Foto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String nome;

    @ManyToOne
    @JoinColumn(name="localId")
    Local local;

    public Foto(){

    }

    public Foto(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
}
