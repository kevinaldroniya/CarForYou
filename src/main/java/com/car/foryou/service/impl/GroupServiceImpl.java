package com.car.foryou.service.impl;

import com.car.foryou.dto.group.GroupResponse;
import com.car.foryou.model.Group;
import com.car.foryou.model.Variant;
import com.car.foryou.repository.GroupRepository;
import com.car.foryou.service.GroupService;
import com.car.foryou.util.mapper.GroupMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    public Page<GroupResponse> getAllGroups(String name, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("name").ascending() : Sort.by("name").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Group> filter = groupRepository.findAllByFilter(name, pageable);
        return filter.map(groupMapper::mapToGroupResponse);
    }

    @Override
    public GroupResponse getGroupByName(String name) {
        Group group = groupRepository.findByName(name).orElseThrow(
                () -> new RuntimeException("Group with given name: " + name + "not found")
        );
        return groupMapper.mapToGroupResponse(group);
    }

    @Override
    public GroupResponse createGroup(String name) {
        groupRepository.findByName(name).ifPresent(group -> {
            throw new RuntimeException("Group with given name : " + name + "was already exists");
        });
        Group group = Group.builder()
                .name(name)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                .createdBy(1L)
                .build();

        Group saved = groupRepository.save(group);

        return groupMapper.mapToGroupResponse(saved);
    }

    @Override
    public GroupResponse updateGroup(long id, String name) {
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Group with given id : " + id + " is not found")
        );
        groupRepository.findByName(name).ifPresent(groupByName -> {
            if (groupByName.getId() != id){
                throw new RuntimeException("Group with given name : " + name + " was already exists");
            }
        });
        group.setName(name);
        group.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
        group.setUpdatedAt(1L);
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }

    @Override
    public GroupResponse deleteGroup(long id) {
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Group with given id : " + id + " is not found")
        );
        group.setDeletedBy(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond());
        group.setDeletedBy(1L);
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }
}
