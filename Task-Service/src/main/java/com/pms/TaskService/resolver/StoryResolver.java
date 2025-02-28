package com.pms.TaskService.resolver;


import com.pms.TaskService.dto.*;
import com.pms.TaskService.services.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@RequiredArgsConstructor
@Slf4j
@Controller
public class StoryResolver {

    private final StoryService storyService;

    /* Creating story */
    @MutationMapping
    public StoryDTO createStory(@Argument("story") StoryDTO storyInputDTO){
        log.info("story is creating");
        return storyService.createStory(storyInputDTO);
    }

    /* Deleting story*/
    @MutationMapping
    public StoryDTO deleteStory(@Argument String storyId){
        return storyService.deleteStory(storyId);
    }

    /* find story by id */
    @QueryMapping
    public StoryDTO getStoryById(@Argument String storyId){
        return storyService.getStoryById(storyId);
    }



}
