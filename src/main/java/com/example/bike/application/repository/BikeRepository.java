package com.example.bike.application.repository;

import com.example.bike.inftrastructure.dbo.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BikeRepository extends JpaRepository<BikeEntity, Long>, JpaSpecificationExecutor<BikeEntity> {
}
