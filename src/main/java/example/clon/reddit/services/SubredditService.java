package example.clon.reddit.services;

import example.clon.reddit.dtos.SubRedditDto;
import example.clon.reddit.entities.Subreddit;
import example.clon.reddit.repositories.SubredditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class SubredditService {

    @Autowired
    SubredditRepository subredditRepository;

    @Transactional
    public SubRedditDto save(SubRedditDto subRedditDto){
        Subreddit save = subredditRepository.save(mapSubreddit(subRedditDto));
        subRedditDto.setId(save.getId());
        return subRedditDto;
    }

    @Transactional(readOnly = true)
    public List<SubRedditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    private SubRedditDto mapToDto(Subreddit subreddit){
        return SubRedditDto.builder()
                .name(subreddit.getName())
                .id(subreddit.getId())
                .numberOfPosts(subreddit.getPosts().size())
                .build();
    }

    private Subreddit mapSubreddit(SubRedditDto subRedditDto) {
        return Subreddit.builder()
                .name(subRedditDto.getName())
                .description(subRedditDto.getDescription())
                .build();
    }
}
