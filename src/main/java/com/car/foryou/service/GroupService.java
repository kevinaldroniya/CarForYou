package com.car.foryou.service;

import com.car.foryou.dto.group.GroupResponse;
import org.springframework.data.domain.Page;


public interface GroupService {
    Page<GroupResponse> getAllGroups(String name, String sortDirection);
    GroupResponse getGroupByName(String name);
    GroupResponse createGroup(String name);
    GroupResponse updateGroup(long id, String name);
    GroupResponse deleteGroup(long id);
}
