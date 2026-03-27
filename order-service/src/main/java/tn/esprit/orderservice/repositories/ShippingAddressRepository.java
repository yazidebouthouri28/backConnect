package tn.esprit.orderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.orderservice.entities.ShippingAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, UUID> {

    List<ShippingAddress> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(UUID userId);
}
