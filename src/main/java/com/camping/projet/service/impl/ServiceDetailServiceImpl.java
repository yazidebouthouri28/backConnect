package com.camping.projet.service.impl;

import com.camping.projet.dto.request.ServiceDetailRequest;
import com.camping.projet.dto.response.ServiceDetailResponse;
import com.camping.projet.entity.mongo.ServiceDetail;
import com.camping.projet.mapper.ServiceDetailMapper;
import com.camping.projet.repository.mongo.ServiceDetailMongoRepository;
import com.camping.projet.service.IServiceDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceDetailServiceImpl implements IServiceDetailService {

    private final ServiceDetailMongoRepository detailRepository;
    private final ServiceDetailMapper detailMapper;
    private final MessageSource messageSource;

    @Override
    public ServiceDetailResponse saveOrUpdateDetail(ServiceDetailRequest request) {
        ServiceDetail detail = detailRepository.findByServiceId(request.getServiceId())
                .orElse(new ServiceDetail());

        detailMapper.updateEntity(request, detail);

        if (detail.getServiceId() == null) {
            detail.setServiceId(request.getServiceId());
            detail.setPhotos(new ArrayList<>());
            detail.setReviews(new ArrayList<>());
            detail.setAverageRating(0.0);
        }

        return detailMapper.toResponse(detailRepository.save(detail));
    }

    @Override
    public ServiceDetailResponse getDetailByServiceId(Long serviceId) {
        return detailRepository.findByServiceId(serviceId)
                .map(detailMapper::toResponse)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("service.detail.error.notfound", null,
                        LocaleContextHolder.getLocale())));
    }

    @Override
    public void addPhoto(Long serviceId, String photoUrl) {
        ServiceDetail detail = detailRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("service.detail.error.notfound", null,
                        LocaleContextHolder.getLocale())));
        detail.getPhotos().add(photoUrl);
        detailRepository.save(detail);
    }

    @Override
    public void addReview(Long serviceId, Long userId, Integer rating, String comment) {
        ServiceDetail detail = detailRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("service.detail.error.notfound", null,
                        LocaleContextHolder.getLocale())));

        ServiceDetail.Review review = new ServiceDetail.Review(userId, rating, comment, LocalDateTime.now());
        detail.getReviews().add(review);

        // Recalculate average rating
        double sum = detail.getReviews().stream().mapToDouble(ServiceDetail.Review::rating).sum();
        detail.setAverageRating(sum / detail.getReviews().size());

        detailRepository.save(detail);
    }

    @Override
    public void updateSpecifications(Long serviceId, Map<String, Object> specs) {
        ServiceDetail detail = detailRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("service.detail.error.notfound", null,
                        LocaleContextHolder.getLocale())));
        detail.getSpecifications().putAll(specs);
        detailRepository.save(detail);
    }
}
