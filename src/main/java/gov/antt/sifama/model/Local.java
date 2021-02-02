package gov.antt.sifama.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Local implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "TroId")
    private Tro tro;

    String numIdentificacao;
    String data;
    String hora;
    String rodovia;
    String pista;
    String kmInicial;
    String kmFinal;
    String sentido;
    String observacao;


    @OneToMany(mappedBy = "local" , fetch = FetchType.EAGER)
    List<Foto> arquivosDeFotos = new ArrayList<>();

    public Local(Long id, Tro tro, String numIdentificacao, String data, String hora, String rodovia, String kmInicial, String kmFinal, String sentido, String pista) {

        this.id = id;
        this.tro = tro;
        this.numIdentificacao = numIdentificacao;
        this.data = data;
        this.hora = hora;
        this.rodovia = rodovia;
        this.kmInicial = kmInicial;
        this.kmFinal = kmFinal;
        this.sentido = sentido;
        this.pista = pista;
    }

    public Local(){

    }

    public String getNumIdentificacao() {
        return numIdentificacao;
    }

    public void setNumIdentificacao(String numIdentificacao) {
        this.numIdentificacao = numIdentificacao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getRodovia() {
        return rodovia;
    }

    public void setRodovia(String rodovia) {
        this.rodovia = rodovia;
    }

    public String getKmInicial() {
        return kmInicial;
    }

    public void setKmInicial(String kmInicial) {
        this.kmInicial = kmInicial;
    }

    public String getKmFinal() {
        return kmFinal;
    }

    public void setKmFinal(String kmFinal) {
        this.kmFinal = kmFinal;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tro getTro() {
        return tro;
    }

    public void setTro(Tro tro) {
        this.tro = tro;
    }

    public List<Foto> getArquivosDeFotos() {
        return arquivosDeFotos;
    }

    public void setArquivosDeFotos(List<Foto> arquivosDeFotos) {
        this.arquivosDeFotos = arquivosDeFotos;
    }

    public String getPista() {
        return pista;
    }

    public void setPista(String pista) {
        this.pista = pista;
    }
}

