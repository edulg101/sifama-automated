package gov.antt.sifama.model.dto;

public class LocalDto {

    Integer id;
    String rodovia;
    String kmInicial;
    String kmFinal;
    String sentido;
    Integer troId;

    public LocalDto(){

    }

    public LocalDto(Integer id, String rodovia, String kmInicial, String kmFinal, String sentido, Integer troId) {
        this.id = id;
        this.rodovia = rodovia;
        this.kmInicial = kmInicial;
        this.kmFinal = kmFinal;
        this.sentido = sentido;
        this.troId = troId;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getTroId() {
        return troId;
    }

    public void setTroId(Integer troId) {
        this.troId = troId;
    }
}
