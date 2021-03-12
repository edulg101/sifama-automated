package gov.antt.sifama.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ImgPath {

    @Id
    Integer id;
    String filePath;

    public ImgPath(){

    }

    public ImgPath(Integer id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
