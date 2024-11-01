package com.car.foryou.controller;

import com.car.foryou.dto.group.GroupRequest;
import com.car.foryou.dto.group.GroupResponse;
import com.car.foryou.service.group.GroupService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController extends BaseApiControllerV1 {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<GroupResponse>> getAllGroupsFilter(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "sortingDirection", defaultValue = "asc") String sortingDirection
    ){
        Page<GroupResponse> groups = groupService.getAllGroupsResponse(name, sortingDirection);
        return ResponseEntity.ok(groups);
    }

    @GetMapping(
            path = "/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GroupResponse> getGroupByName(@PathVariable("name") String name){
        GroupResponse response = groupService.getGroupResponseByName(name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupRequest request){
        GroupResponse response = groupService.createGroup(request.name());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GroupResponse> updateGroupById(@PathVariable("id") int id, @RequestBody GroupRequest request){
        GroupResponse response = groupService.updateGroup(id, request.name());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(
            path = "/{id}"
    )
    public ResponseEntity<GroupResponse> deleteGroupById(@PathVariable("id") int id){
        GroupResponse response = groupService.deleteGroup(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
