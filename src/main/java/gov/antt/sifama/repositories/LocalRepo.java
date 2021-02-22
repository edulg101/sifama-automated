package gov.antt.sifama.repositories;

import gov.antt.sifama.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepo extends JpaRepository<Local, Integer> {
}
