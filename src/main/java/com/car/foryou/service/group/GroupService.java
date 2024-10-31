package com.car.foryou.service.group;

import com.car.foryou.dto.group.GroupResponse;
import com.car.foryou.model.Group;
import org.springframework.data.domain.Page;

import java.util.List;


public interface GroupService {
    List<Group> getAllGroups();
    Group getGroupById(Integer id);
    Group getGroupByName(String groupName);
    Page<GroupResponse> getAllGroupsResponse(String name, String sortDirection);
    GroupResponse getGroupResponseByName(String name);
    GroupResponse createGroup(String name);
    GroupResponse updateGroup(int id, String name);
    GroupResponse deleteGroup(int id);
}
