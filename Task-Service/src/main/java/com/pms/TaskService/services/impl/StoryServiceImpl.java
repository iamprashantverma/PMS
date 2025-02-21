package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.StoryDTO;
import com.pms.TaskService.dto.StoryInputDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.Story;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.StoryRepository;
import com.pms.TaskService.services.StoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Controller
public class StoryServiceImpl implements StoryService {

    private final ModelMapper modelMapper;
    private final StoryRepository storyRepository;
    private final EpicRepository epicRepository;

    @Override
    public boolean isExist(String storyId) {
        boolean flag =  storyRepository.existsById(storyId);
        if(!flag){
            throw new ResourceNotFound("Story not exist with id "+storyId);
        }
        return true;
    }

    /* creating story */
    @Override
    public StoryDTO createStory(StoryInputDTO storyInputDTO) {
        Story story = modelMapper.map(storyInputDTO, Story.class);
        Story savedStory = storyRepository.save(story);

        log.info("Story created with title {}", savedStory.getTitle());
        //TODO add current user as creater
        return modelMapper.map(savedStory, StoryDTO.class);
    }

    @Override
    public StoryDTO updateStory(String storyId, StoryInputDTO story) {
        return null;
    }

    @Override
    public StoryDTO deleteStory(String storyId) {
        Story story = modelMapper.map(getStoryById(storyId), Story.class);
        storyRepository.deleteById(storyId);
        log.info("Epic deleted with id {}", storyId);
        // TODO also have to  delete from issues where it is saved
        return modelMapper.map(story, StoryDTO.class);
    }

    @Override
    public StoryDTO getStoryById(String storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(()-> new ResourceNotFound("Story not found with id "+ storyId));
        return modelMapper.map(story, StoryDTO.class);
    }

    @Override
    @Transactional
    public ResponseDTO addStoryOnEpic(String epicId, String storyId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(()-> new ResourceNotFound("Epic not found with id "+epicId));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(()-> new ResourceNotFound("Story not found with id "+storyId));

        story.setEpic(epic);
        Story savedStory = storyRepository.save(story);
        epic.getStories().add(savedStory);
        epicRepository.save(epic);
        log.info(String.valueOf(epic));
        return new ResponseDTO("Story added on Epic successfully");
    }
}
