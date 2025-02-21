package com.pms.TaskService.services;

import com.pms.TaskService.dto.*;

public interface StoryService {

    boolean isExist(String storyId);

    StoryDTO createStory(StoryInputDTO storyInputDTO);

    StoryDTO updateStory(String storyId, StoryInputDTO story);

    StoryDTO deleteStory(String storyId);

    StoryDTO getStoryById(String storyId);

    ResponseDTO addStoryOnEpic(String epicId, String storyId);
}
