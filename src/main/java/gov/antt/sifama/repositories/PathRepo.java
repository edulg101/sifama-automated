package gov.antt.sifama.repositories;

import gov.antt.sifama.model.ImgPath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PathRepo extends JpaRepository<ImgPath, Integer> {
}
