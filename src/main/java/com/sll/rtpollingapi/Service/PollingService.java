package com.sll.rtpollingapi.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.sll.rtpollingapi.Standards.PollPolicy;



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
        Poll poll = new Poll(ownerId, dto.expiryDate(),dto.topic(), dto.policy());
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
        List<Poll> polls = polldb.myPolls(ownerId,p).getContent();
        List<PollHeaderDTO> list = new ArrayList<>();
        for(Poll poll : polls){
            PollHeaderDTO dto = new PollHeaderDTO(poll.getId(),poll.getExpiryDate(),poll.getTopic());
            list.add(dto);
        }
        return list;
    }
    public List<PollHeaderDTO> viewOtherPolls(int ownerId,int page,int size,Sort sort){
        Pageable p = PageRequest.of(page, size, sort);
        List<Poll> polls = polldb.notMyPolls(ownerId,p).getContent();
        List<PollHeaderDTO> list = new ArrayList<>();
        for(Poll poll : polls){
            PollHeaderDTO dto = new PollHeaderDTO(poll.getId(),poll.getExpiryDate(),poll.getTopic());
            list.add(dto);
        }
        return list;
    }
    public void vote(int voterId,int pollId,int optionId) throws GeneralException{
        Poll poll = polldb.getSpecificPoll(pollId).orElseThrow(()->new GeneralException("400:No Such Poll Exists"));
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
        sse.broadcast(pollId,getPollDTO(poll,true));
    }
    public List<PollHeaderDTO> viewPolls(Integer page, Integer size, Sort sort) {
        Pageable p = PageRequest.of(page, size, sort);
        List<Poll> polls = polldb.findAll(p).getContent();
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
    public Object newClient(int userId,int pollId) throws GeneralException {
        Poll poll = polldb.getSpecificPoll(pollId).orElseThrow(() -> new GeneralException("400:No Such Poll Exists"));
        boolean poll_is_expired = poll.getExpiryDate().isBefore(LocalDateTime.now());
        SseEmitter emitter = null;
        switch(poll.getPolicy()){
            case PollPolicy.NO_RESTRICTION :
                    if(poll_is_expired) return getPollDTO(poll,true);
                    emitter = sse.subscribe(pollId);
                    sse.unicast(pollId,emitter, getPollDTO(poll,true));
                    return emitter;
            case PollPolicy.RESULTS_AFTER_END :
                    if(poll_is_expired || poll.getOwnerId() != userId) return getPollDTO(poll, poll_is_expired);
                    emitter = sse.subscribe(pollId);
                    sse.unicast(pollId,emitter, getPollDTO(poll,true));
                    return emitter;
            case PollPolicy.RESULTS_OWNER_ONLY :
                    if(poll.getOwnerId() != userId) return getPollDTO(poll, false);
                    if(poll_is_expired) return getPollDTO(poll, true);
                    emitter = sse.subscribe(pollId);
                    sse.unicast(pollId,emitter, getPollDTO(poll,true));
                    return emitter;
        }
        return "ERROR";
    }
    private PollResponseDTO getPollDTO(Poll poll,boolean is_vote_visible){
        List<OptionDTO> optionsDTO = new ArrayList<>();
        for(Option option:optiondb.findByPollId(poll.getId())){
            Integer vote = option.getVote();
            if(!is_vote_visible) vote = null;
            OptionDTO opDTO = new OptionDTO(option.getId(), option.getData(),vote);
            optionsDTO.add(opDTO);
        }
        PollResponseDTO dto = new PollResponseDTO(poll.getId(),poll.getExpiryDate(),poll.getCreatedAt(),poll.getTopic(),optionsDTO);
        return dto;
    }
}
