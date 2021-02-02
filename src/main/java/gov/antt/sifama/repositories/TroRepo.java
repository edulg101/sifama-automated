package gov.antt.sifama.repositories;

import gov.antt.sifama.model.Tro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TroRepo extends JpaRepository<Tro, Long> {
}
