package tn.esprit.projetintegre.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long missionId;
    private String missionName;
    private Integer currentProgress;
    private Integer targetValue;
    private Boolean isCompleted;
    private Boolean rewardClaimed;
    private Integer rewardPoints;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime rewardClaimedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Boolean getRewardClaimed() {
        return rewardClaimed;
    }

    public void setRewardClaimed(Boolean rewardClaimed) {
        this.rewardClaimed = rewardClaimed;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getRewardClaimedAt() {
        return rewardClaimedAt;
    }

    public void setRewardClaimedAt(LocalDateTime rewardClaimedAt) {
        this.rewardClaimedAt = rewardClaimedAt;
    }

    public static UserMissionResponseBuilder builder() {
        return new UserMissionResponseBuilder();
    }

    public static class UserMissionResponseBuilder {
        private Long id;
        private Long userId;
        private String userName;
        private Long missionId;
        private String missionName;
        private Integer currentProgress;
        private Integer targetValue;
        private Boolean isCompleted;
        private Boolean rewardClaimed;
        private Integer rewardPoints;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private LocalDateTime rewardClaimedAt;

        public UserMissionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserMissionResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserMissionResponseBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserMissionResponseBuilder missionId(Long missionId) {
            this.missionId = missionId;
            return this;
        }

        public UserMissionResponseBuilder missionName(String missionName) {
            this.missionName = missionName;
            return this;
        }

        public UserMissionResponseBuilder currentProgress(Integer currentProgress) {
            this.currentProgress = currentProgress;
            return this;
        }

        public UserMissionResponseBuilder targetValue(Integer targetValue) {
            this.targetValue = targetValue;
            return this;
        }

        public UserMissionResponseBuilder isCompleted(Boolean isCompleted) {
            this.isCompleted = isCompleted;
            return this;
        }

        public UserMissionResponseBuilder rewardClaimed(Boolean rewardClaimed) {
            this.rewardClaimed = rewardClaimed;
            return this;
        }

        public UserMissionResponseBuilder rewardPoints(Integer rewardPoints) {
            this.rewardPoints = rewardPoints;
            return this;
        }

        public UserMissionResponseBuilder startedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public UserMissionResponseBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public UserMissionResponseBuilder rewardClaimedAt(LocalDateTime rewardClaimedAt) {
            this.rewardClaimedAt = rewardClaimedAt;
            return this;
        }

        public UserMissionResponse build() {
            UserMissionResponse response = new UserMissionResponse();
            response.setId(id);
            response.setUserId(userId);
            response.setUserName(userName);
            response.setMissionId(missionId);
            response.setMissionName(missionName);
            response.setCurrentProgress(currentProgress);
            response.setTargetValue(targetValue);
            response.setIsCompleted(isCompleted);
            response.setRewardClaimed(rewardClaimed);
            response.setRewardPoints(rewardPoints);
            response.setStartedAt(startedAt);
            response.setCompletedAt(completedAt);
            response.setRewardClaimedAt(rewardClaimedAt);
            return response;
        }
    }
}
