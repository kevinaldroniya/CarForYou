package com.car.foryou.service.group;

import com.car.foryou.dto.group.GroupResponse;
import com.car.foryou.model.Group;
import com.car.foryou.repository.GroupRepository;
import com.car.foryou.mapper.GroupMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
                .build();

        Group saved = groupRepository.save(group);

        return groupMapper.mapToGroupResponse(saved);
    }

    @Override
    public GroupResponse updateGroup(int id, String name) {
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Group with given id : " + id + " is not found")
        );
        groupRepository.findByName(name).ifPresent(groupByName -> {
            if (groupByName.getId() != id){
                throw new RuntimeException("Group with given name : " + name + " was already exists");
            }
        });
        group.setName(name);
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }

    @Override
    public GroupResponse deleteGroup(int id) {
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Group with given id : " + id + " is not found")
        );
        group.setDeletedAt(Instant.now());
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }
}
