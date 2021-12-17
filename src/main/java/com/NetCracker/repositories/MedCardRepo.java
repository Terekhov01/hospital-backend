package com.NetCracker.repositories;


import com.NetCracker.entities.MedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedCardRepo extends JpaRepository<MedCard, Integer> {
}
