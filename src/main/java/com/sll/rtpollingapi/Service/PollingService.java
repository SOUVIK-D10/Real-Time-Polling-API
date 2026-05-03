package com.sll.rtpollingapi.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sll.rtpollingapi.DTO.OptionDTO;
import com.sll.rtpollingapi.DTO.PollHeaderDTO;
import com.sll.rtpollingapi.DTO.PollRequestDTO;
import com.sll.rtpollingapi.DTO.PollResponseDTO;
import com.sll.rtpollingapi.Exception.GeneralException;
import com.sll.rtpollingapi.Model.Option;
import com.sll.rtpollingapi.Model.Poll;
import com.sll.rtpollingapi.Model.Vote;
import com.sll.rtpollingapi.Repo.OptionRepo;
import com.sll.rtpollingapi.Repo.PollRepo;
import com.sll.rtpollingapi.Repo.VoteRepo;



@Service
public class PollingService {
    private PollRepo polldb;
    private OptionRepo optiondb;
    private VoteRepo votedb;
    private SSEService sse;
    @Autowired
	public PollingService(PollRepo polldb, OptionRepo optiondb, VoteRepo votedb,SSEService sse) {
		this.polldb = polldb;
		this.optiondb = optiondb;
		this.votedb = votedb;
        this.sse=sse;
	}
    public PollResponseDTO createPoll(int ownerId,PollRequestDTO dto){
        Poll poll = new Poll(ownerId, dto.expiryDate(),dto.topic(), 0);
        poll = polldb.save(poll);
        List<Option> options = new ArrayList<>();
        List<OptionDTO> optionsDTO = new ArrayList<>();
        for(String data:dto.options()){
            Option option = new Option(poll.getId(), data);
            options.add(option);
        }
        options = optiondb.saveAll(options);
        for(Option option:options){
            OptionDTO opDTO = new OptionDTO(option.getId(), option.getData(), option.getVote());
            optionsDTO.add(opDTO);
        }
        return new PollResponseDTO(poll.getId(), poll.getExpiryDate(), poll.getCreatedAt(), poll.getTopic(), optionsDTO);
    }
    public List<PollHeaderDTO> viewMyPolls(int ownerId,int page,int size,Sort sort){
        Pageable p = PageRequest.of(page, size, sort);
        List<Poll> polls = polldb.myPolls(ownerId,p);
        List<PollHeaderDTO> list = new ArrayList<>();
        for(Poll poll : polls){
            PollHeaderDTO dto = new PollHeaderDTO(poll.getId(),poll.getExpiryDate(),poll.getTopic());
            list.add(dto);
        }
        return list;
    }
    public List<PollHeaderDTO> viewOtherPolls(int ownerId,int page,int size,Sort sort){
        Pageable p = PageRequest.of(page, size, sort);
        List<Poll> polls = polldb.notMyPolls(ownerId,p);
        List<PollHeaderDTO> list = new ArrayList<>();
        for(Poll poll : polls){
            PollHeaderDTO dto = new PollHeaderDTO(poll.getId(),poll.getExpiryDate(),poll.getTopic());
            list.add(dto);
        }
        return list;
    }
    public void vote(int voterId,int pollId,int optionId) throws GeneralException{
        Poll poll = polldb.getReferenceById(pollId);
        if(poll==null) throw new GeneralException("400:No such poll existes");
        if(poll.getExpiryDate().isBefore(LocalDateTime.now()))
        throw new GeneralException("410: Poll expired");
        Vote vote = votedb.findByVoterIdAndPollId(voterId,pollId);
        if(vote == null){
            optiondb.vote(pollId,optionId);
            vote = new Vote(voterId, pollId, optionId);
        }
        else{
            optiondb.cancel(vote.getOptionId(),pollId);
            optiondb.vote(pollId, optionId);
            vote.setOptionId(optionId);
        }
        votedb.save(vote);
        sse.broadcast(pollId,getPollById(pollId));
    }
    public List<PollHeaderDTO> viewPolls(Integer page, Integer size, Sort sort) {
        List<Poll> polls = polldb.findAll();
        List<PollHeaderDTO> list = new ArrayList<>();
        for(Poll poll : polls){
            PollHeaderDTO dto = new PollHeaderDTO(poll.getId(),poll.getExpiryDate(),poll.getTopic());
            list.add(dto);
        }
        return list;
    }
    public void delete(int userId, int pollId) throws GeneralException {
        int check1 = polldb.deleteByIdAndOwnerId(pollId,userId);
        if(check1==0) throw new GeneralException("400:No Such Poll exists");
        optiondb.deleteAllByPollId(pollId);
        votedb.deleteAllByPollId(pollId);
    }
    public SseEmitter newClient(int pollId) {
        SseEmitter emitter = sse.subscribe(pollId);
        sse.unicast(pollId,emitter, getPollById(pollId));
        return emitter;
    }
    private PollResponseDTO getPollById(int id){
        Poll poll = polldb.getReferenceById(id);
        List<OptionDTO> optionsDTO = new ArrayList<>();
        for(Option option:optiondb.findByPollId(poll.getId())){
        OptionDTO opDTO = new OptionDTO(option.getId(), option.getData(), option.getVote());
        optionsDTO.add(opDTO);
        }
        PollResponseDTO dto = new PollResponseDTO(poll.getId(),poll.getExpiryDate(),poll.getCreatedAt(),poll.getTopic(),optionsDTO);
        return dto;
    }
}
