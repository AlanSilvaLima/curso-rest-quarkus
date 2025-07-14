package io.github.alansilva.quarkussocial.rest.dto;

import io.github.alansilva.quarkussocial.domain.model.Follower;
import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {
    private Integer followersCount;
    private List<FollowerResponse> content;
}
