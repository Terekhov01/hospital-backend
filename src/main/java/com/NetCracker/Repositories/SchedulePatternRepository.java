package com.NetCracker.Repositories;

import com.NetCracker.Entities.SchedulePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulePatternRepository extends JpaRepository<SchedulePattern, Long>
{
}
