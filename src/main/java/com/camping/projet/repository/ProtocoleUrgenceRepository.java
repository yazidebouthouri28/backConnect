package com.camping.projet.repository;

import com.camping.projet.entity.ProtocoleUrgence;
import com.camping.projet.enums.TypeUrgence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProtocoleUrgenceRepository extends JpaRepository<ProtocoleUrgence, Long> {
    List<ProtocoleUrgence> findByType(TypeUrgence type);

    List<ProtocoleUrgence> findByGraviteGreaterThanEqual(int gravite);
}
