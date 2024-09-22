package com.car.foryou.util.mapper;

import com.car.foryou.dto.group.GroupResponse;
import com.car.foryou.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupResponse mapToGroupResponse(Group group){
        try {
            return GroupResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error while mapping Group to GroupResponse");
        }
    }
}
