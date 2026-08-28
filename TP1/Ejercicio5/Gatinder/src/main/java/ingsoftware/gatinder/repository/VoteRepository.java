package ingsoftware.gatinder.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ingsoftware.gatinder.entity.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {
    @Query("SELECT v FROM Vote v WHERE v.voter.id = :id")
    public List<Vote> findSelfVotes(@Param("id") String id);

    @Query("SELECT v FROM Vote v WHERE v.voted.id = :id")
    public List<Vote> findReceivedVotes(@Param("id") String id);
}
