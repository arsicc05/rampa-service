package com.rampa.rampa.repository;

import com.rampa.rampa.model.Cenovnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CenovnikRepository extends JpaRepository<Cenovnik, Long> {

    Optional<Cenovnik> findByIsValidTrue();

    @Modifying
    @Query("UPDATE Cenovnik c SET c.isValid = false WHERE c.isValid = true")
    void invalidateAllCenovniks();
}
