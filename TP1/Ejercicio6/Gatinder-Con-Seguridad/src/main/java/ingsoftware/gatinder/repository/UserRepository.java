package ingsoftware.gatinder.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ingsoftware.gatinder.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    User findByFirstName(String firstName);
    Optional<User> findByRememberToken(String rememberToken);

    @Query("SELECT u FROM User u WHERE u.zone.id = :id AND u.deleted = false")
    public List<User> findByZoneId(@Param("id") Long id);
}
