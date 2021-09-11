package com.project.bokduck.repository;

import com.project.bokduck.domain.ElectronicDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ElectronicDevicesRepository extends JpaRepository<ElectronicDevices, Long> {
}
