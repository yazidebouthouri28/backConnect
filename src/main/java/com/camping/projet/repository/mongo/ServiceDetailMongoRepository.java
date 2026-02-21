package com.camping.projet.repository.mongo;

import com.camping.projet.entity.mongo.ServiceDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository("serviceDetailMongoRepository")
public interface ServiceDetailMongoRepository extends MongoRepository<ServiceDetail, String> {
    Optional<ServiceDetail> findByServiceId(Long serviceId);

    void deleteByServiceId(Long serviceId);
}
