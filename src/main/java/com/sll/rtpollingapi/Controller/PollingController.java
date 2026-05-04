package com.sll.rtpollingapi.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sll.rtpollingapi.DTO.PollHeaderDTO;
import com.sll.rtpollingapi.DTO.PollRequestDTO;
import com.sll.rtpollingapi.DTO.PollResponseDTO;
import com.sll.rtpollingapi.Exception.GeneralException;
import com.sll.rtpollingapi.Model.UserData;
import com.sll.rtpollingapi.Service.PollingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/polls")
public class PollingController {
    private PollingService service;
    @Autowired
    public PollingController(PollingService service){
        this.service=service;
    }
    @GetMapping("/all/my")
    public ResponseEntity<List<PollHeaderDTO>> getAllMy(
        @AuthenticationPrincipal UserData details,
        @RequestParam(value = "page",required = false,defaultValue = "0") @NotNull Integer page,
        @RequestParam(value = "size",required = false, defaultValue = "5") @NotNull Integer size,
        @RequestParam(value = "sortby",required = false, defaultValue = "id") @NotNull String sortby,
        @RequestParam(value = "dir",required = false, defaultValue = "asc") @NotNull String dir
    ){
        Sort sort=null;
        if(size > 10 ) size=10;
        if(size < 1 ) size=5;
        if(dir.equalsIgnoreCase("desc")) sort = Sort.by(sortby).descending();
        else sort = Sort.by(sortby).ascending();
        return new ResponseEntity<>(service.viewMyPolls(details.getUserId(),page,size,sort),HttpStatus.OK);
    }
    @GetMapping("/all/others")
    public ResponseEntity<List<PollHeaderDTO>> getAllOthers(
        @AuthenticationPrincipal UserData details,
        @RequestParam(value = "page",required = false,defaultValue = "0") @NotNull Integer page,
        @RequestParam(value = "size",required = false, defaultValue = "5") @NotNull Integer size,
        @RequestParam(value = "sortby",required = false, defaultValue = "id") @NotNull String sortby,
        @RequestParam(value = "dir",required = false, defaultValue = "asc") @NotNull String dir
    ){
        Sort sort=null;
        if(size > 10 ) size=10;
        if(size < 1 ) size=5;
        if(dir.equalsIgnoreCase("desc")) sort = Sort.by(sortby).descending();
        else sort = Sort.by(sortby).ascending();
        return new ResponseEntity<>(service.viewOtherPolls(details.getUserId(),page,size,sort),HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<List<PollHeaderDTO>> getAll(
        @RequestParam(value = "page",required = false,defaultValue = "0") @NotNull Integer page,
        @RequestParam(value = "size",required = false, defaultValue = "5") @NotNull Integer size,
        @RequestParam(value = "sortby",required = false, defaultValue = "id") @NotNull String sortby,
        @RequestParam(value = "dir",required = false, defaultValue = "asc") @NotNull String dir
    ){
        Sort sort=null;
        if(size > 10 ) size=10;
        if(size < 1 ) size=5;
        if(dir.equalsIgnoreCase("desc")) sort = Sort.by(sortby).descending();
        else sort = Sort.by(sortby).ascending();
        return new ResponseEntity<>(service.viewPolls(page,size,sort),HttpStatus.OK);
    }
    @GetMapping("/{pollId}/details")
    public ResponseEntity<?> getLivePollDataById(
        @AuthenticationPrincipal UserData details,
        @PathVariable int pollId
    ){
        return new ResponseEntity<>(service.newClient(details.getUserId(),pollId),HttpStatus.OK);
    }
    @PostMapping("/new")
    public ResponseEntity<PollResponseDTO> newPoll(
        @AuthenticationPrincipal UserData details,
        @RequestBody @Valid PollRequestDTO dto
    ){
        return new ResponseEntity<>(service.createPoll(details.getUserId(), dto),HttpStatus.CREATED);
    }
    @DeleteMapping("/delete/{pollId}")
    public ResponseEntity<Void> vote(
        @AuthenticationPrincipal UserData details,
        @PathVariable int pollId
    ) throws GeneralException{
        service.delete(details.getUserId(),pollId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PatchMapping("/{pollId}/vote/{optionId}")
    public ResponseEntity<Void> vote(
        @AuthenticationPrincipal UserData details,
        @PathVariable int pollId,
        @PathVariable int optionId
    ) throws GeneralException{
        service.vote(details.getUserId(), pollId, optionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
