package ingsoftware.gatinder.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ingsoftware.gatinder.entity.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    public List<Pet> findByDeletedFalse();

    @Query("SELECT p FROM Pet p WHERE p.user.id = :id AND p.deleted = false")
    public List<Pet> findPetsByUser(@Param("id")String id);

    @Query("SELECT p FROM Pet p WHERE p.user.id = :id AND p.name LIKE %:name% AND p.deleted = false")
    public List<Pet> findPetByName(@Param("id")String id,@Param("name")String name);
}
