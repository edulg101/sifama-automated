package gov.antt.sifama.model.dto;


import java.util.ArrayList;
import java.util.List;

public class TroDto {

    Integer id;
    String obs;
    String prazo;

    List<LocalDto> localList = new ArrayList<>();

    public TroDto(){

    }

    public TroDto(Integer id, String obs, String prazo) {
        this.id = id;
        this.obs = obs;
        this.prazo = prazo;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<LocalDto> getLocalList() {
        return localList;
    }
}
