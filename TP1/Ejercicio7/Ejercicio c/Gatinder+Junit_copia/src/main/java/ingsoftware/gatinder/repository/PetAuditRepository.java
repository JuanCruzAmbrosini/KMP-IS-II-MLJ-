package ingsoftware.gatinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import ingsoftware.gatinder.entity.PetAudit;

@Repository
public interface PetAuditRepository extends JpaRepository<PetAudit, Long> {
	List<PetAudit> findByUserIdOrderByOccurredAtDesc(String userId);
}
