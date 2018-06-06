package com.zz.flight.repository;

import com.zz.flight.entity.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;


public interface RequestRepository extends JpaRepository<Request,Long> {
    Page<Request> findByTimeBetween(Date start, Date end, Pageable pageable);

    Page<Request> findAllByStatus(int status,Pageable pageable);

    Request findByRequestUserIdAndStatus(Long id,int status);

    Page<Request> findAllByRequestUserIdAndStatusNotOrTakenUserIdAndStatusNot(Long requestUserId,Integer status1,Long takenUserId,Integer status2,Pageable pageable);

    Page<Request> findAllByStatusGreaterThanEqual(int status,Pageable pageable);
}
