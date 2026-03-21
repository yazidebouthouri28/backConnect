package tn.esprit.projetintegre.dto.response;

import lombok.*;
import tn.esprit.projetintegre.enums.MissionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {
    private Long id;
    private String name;
    private String description;
    private MissionType type;
    private Integer targetValue;
    private Integer rewardPoints;
    private String rewardBadge;
    private String rewardDescription;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Boolean isRepeatable;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MissionType getType() {
        return type;
    }

    public void setType(MissionType type) {
        this.type = type;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getRewardBadge() {
        return rewardBadge;
    }

    public void setRewardBadge(String rewardBadge) {
        this.rewardBadge = rewardBadge;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsRepeatable() {
        return isRepeatable;
    }

    public void setIsRepeatable(Boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static MissionResponseBuilder builder() {
        return new MissionResponseBuilder();
    }

    public static class MissionResponseBuilder {
        private Long id;
        private String name;
        private String description;
        private MissionType type;
        private Integer targetValue;
        private Integer rewardPoints;
        private String rewardBadge;
        private String rewardDescription;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean isActive;
        private Boolean isRepeatable;
        private String category;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public MissionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MissionResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MissionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MissionResponseBuilder type(MissionType type) {
            this.type = type;
            return this;
        }

        public MissionResponseBuilder targetValue(Integer targetValue) {
            this.targetValue = targetValue;
            return this;
        }

        public MissionResponseBuilder rewardPoints(Integer rewardPoints) {
            this.rewardPoints = rewardPoints;
            return this;
        }

        public MissionResponseBuilder rewardBadge(String rewardBadge) {
            this.rewardBadge = rewardBadge;
            return this;
        }

        public MissionResponseBuilder rewardDescription(String rewardDescription) {
            this.rewardDescription = rewardDescription;
            return this;
        }

        public MissionResponseBuilder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public MissionResponseBuilder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public MissionResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public MissionResponseBuilder isRepeatable(Boolean isRepeatable) {
            this.isRepeatable = isRepeatable;
            return this;
        }

        public MissionResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public MissionResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MissionResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MissionResponse build() {
            MissionResponse response = new MissionResponse();
            response.setId(id);
            response.setName(name);
            response.setDescription(description);
            response.setType(type);
            response.setTargetValue(targetValue);
            response.setRewardPoints(rewardPoints);
            response.setRewardBadge(rewardBadge);
            response.setRewardDescription(rewardDescription);
            response.setStartDate(startDate);
            response.setEndDate(endDate);
            response.setIsActive(isActive);
            response.setIsRepeatable(isRepeatable);
            response.setCategory(category);
            response.setCreatedAt(createdAt);
            response.setUpdatedAt(updatedAt);
            return response;
        }
    }
}
