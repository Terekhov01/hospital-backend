package com.NetCracker.Repositories;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.print.Doc;

public class ScheduleSpecifications
{
    private static final Specification<DoctorSchedule> alwaysTrueSpecification = new Specification<DoctorSchedule>()
    {
        @Override
        public Predicate toPredicate(@NotNull Root<DoctorSchedule> root, @NotNull CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
        {
            //Always true
            return criteriaBuilder.and();
        }
    };

    public static Specification<DoctorSchedule> schedulesByDoctor(Integer id)
    {
        if (id == null)
        {
            return alwaysTrueSpecification;
        }
        else
        {
            return new Specification<DoctorSchedule>()
            {
                @Override
                public Predicate toPredicate(@NotNull Root<DoctorSchedule> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder)
                {
                    return criteriaBuilder.equal(root.get("id"), id);
                }
            };
        }
    }
}
