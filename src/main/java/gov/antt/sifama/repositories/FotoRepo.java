package gov.antt.sifama.repositories;

import gov.antt.sifama.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepo extends JpaRepository<Foto, Long> {
}
