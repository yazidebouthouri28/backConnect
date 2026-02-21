package com.camping.projet.repository;

import com.camping.projet.entity.Service;
import com.camping.projet.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByCampingId(Long campingId);

    List<Service> findByServiceType(ServiceType serviceType);

    @Query("SELECT s FROM Service s WHERE s.campingId = :campingId AND s.available = true")
    List<Service> findAvailableByCampingId(@Param("campingId") Long campingId);

    @Query("SELECT s FROM Service s WHERE s.price <= :maxPrice AND s.available = true")
    List<Service> findByMaxPrice(@Param("maxPrice") Double maxPrice);

    @Query("SELECT COUNT(s) > 0 FROM Service s WHERE s.name = :name AND s.campingId = :campingId")
    boolean existsByNameAndCampingId(@Param("name") String name, @Param("campingId") Long campingId);
}
