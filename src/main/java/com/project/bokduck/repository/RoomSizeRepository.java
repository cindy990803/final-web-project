package com.project.bokduck.repository;


import com.project.bokduck.domain.RoomSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomSizeRepository extends JpaRepository<RoomSize, Long> {
}
