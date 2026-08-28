package ingsoftware.gatinder.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ingsoftware.gatinder.entity.Zone;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, String> {
    public List<Zone> findByDeletedFalse();

    public Zone findByName(String name);
}
