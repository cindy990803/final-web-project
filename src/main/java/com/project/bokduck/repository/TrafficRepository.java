package com.project.bokduck.repository;

import domain.Traffic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficRepository extends JpaRepository<Traffic, Long> {
}
