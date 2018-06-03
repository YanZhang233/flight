package com.zz.flight.repository;

import com.zz.flight.entity.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer,Long> {
    Page<Volunteer> findAllByStatus(int status, Pageable pageable);

    Page<Volunteer> findAll(Pageable pageable);
}
