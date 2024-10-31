package com.car.foryou.service.group;

import com.car.foryou.dto.group.GroupResponse;
import com.car.foryou.exception.ResourceAlreadyExistsException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.model.Group;
import com.car.foryou.repository.group.GroupRepository;
import com.car.foryou.mapper.GroupMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    private static final String GROUP = "GROUP";

    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Group getGroupById(Integer id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(GROUP, "ID", id)
        );
    }

    @Override
    public Group getGroupByName(String groupName) {
        return groupRepository.findByName(groupName).orElseThrow(
                () -> new ResourceNotFoundException(GROUP, "Name", groupName)
        );
    }

    @Override
    public Page<GroupResponse> getAllGroupsResponse(String name, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("name").ascending() : Sort.by("name").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Group> filter = groupRepository.findAllByFilter(name, pageable);
        return filter.map(groupMapper::mapToGroupResponse);
    }

    @Override
    public GroupResponse getGroupResponseByName(String name) {
        Group group = getGroupByName(name);
        return groupMapper.mapToGroupResponse(group);
    }

    @Override
    public GroupResponse createGroup(String name) {
        groupRepository.findByName(name).ifPresent(group -> {
            throw new ResourceAlreadyExistsException(GROUP, HttpStatus.CONFLICT);
        });
        Group group = Group.builder()
                .name(name)
                .build();
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }

    @Override
    public GroupResponse updateGroup(int id, String name) {
        Group group = getGroupById(id);
        groupRepository.findByName(name).ifPresent(groupByName -> {
            if (groupByName.getId() != id){
                throw new ResourceAlreadyExistsException(GROUP, HttpStatus.CONFLICT);
            }
        });
        group.setName(name);
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }

    @Override
    public GroupResponse deleteGroup(int id) {
        Group group = getGroupById(id);
        group.setDeletedAt(Instant.now());
        Group saved = groupRepository.save(group);
        return groupMapper.mapToGroupResponse(saved);
    }
}
