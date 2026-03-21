package tn.esprit.projetintegre.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {
    private Long id;
    private String name;
    private String description;
    private String badge;
    private String icon;
    private Integer requiredPoints;
    private Integer rewardPoints;
    private String category;
    private Integer level;
    private Boolean isActive;
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

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(Integer requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public static AchievementResponseBuilder builder() {
        return new AchievementResponseBuilder();
    }

    public static class AchievementResponseBuilder {
        private Long id;
        private String name;
        private String description;
        private String badge;
        private String icon;
        private Integer requiredPoints;
        private Integer rewardPoints;
        private String category;
        private Integer level;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public AchievementResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AchievementResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AchievementResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AchievementResponseBuilder badge(String badge) {
            this.badge = badge;
            return this;
        }

        public AchievementResponseBuilder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public AchievementResponseBuilder requiredPoints(Integer requiredPoints) {
            this.requiredPoints = requiredPoints;
            return this;
        }

        public AchievementResponseBuilder rewardPoints(Integer rewardPoints) {
            this.rewardPoints = rewardPoints;
            return this;
        }

        public AchievementResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public AchievementResponseBuilder level(Integer level) {
            this.level = level;
            return this;
        }

        public AchievementResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public AchievementResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AchievementResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AchievementResponse build() {
            AchievementResponse response = new AchievementResponse();
            response.setId(id);
            response.setName(name);
            response.setDescription(description);
            response.setBadge(badge);
            response.setIcon(icon);
            response.setRequiredPoints(requiredPoints);
            response.setRewardPoints(rewardPoints);
            response.setCategory(category);
            response.setLevel(level);
            response.setIsActive(isActive);
            response.setCreatedAt(createdAt);
            response.setUpdatedAt(updatedAt);
            return response;
        }
    }
}
