package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.EpicInputDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.IssueRepository;
import com.pms.TaskService.services.EpicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpicServiceImpl implements EpicService {

    private final ModelMapper modelMapper;
    private final EpicRepository epicRepository;
    private final IssueRepository issueRepository;


    /* Check Epic existence */
    @Override
    public boolean isExist(String epicId) {
        boolean flag =  epicRepository.existsById(epicId);
        if(!flag){
            throw new ResourceNotFound("Epic not exist with id "+epicId);
        }
        return true;
    }

    /* Create Epic */
    @Override
    public EpicDTO createEpic(EpicInputDTO inputEpic) {
        Epic epic = modelMapper.map(inputEpic, Epic.class);
        Epic savedEpic = epicRepository.save(epic);
        log.info("Epic created with title {}", savedEpic.getTitle());
        //TODO add current user as creater and add belong project
        return modelMapper.map(savedEpic, EpicDTO.class);
    }

    /* Update Epic */
    @Override
    public EpicDTO updateEpic(String epicId, EpicInputDTO epic) {
        return null;
    }

    /* Delete Epic */
    @Override
    public EpicDTO deleteEpic(String epicId) {
        Epic epic = modelMapper.map(getEpicById(epicId), Epic.class);
        epicRepository.deleteById(epicId);
        log.info("Epic deleted with id {}", epicId);
        return modelMapper.map(epic, EpicDTO.class);
    }


    /* Find epic by Id */
    @Override
    public EpicDTO getEpicById(String epicId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(()-> new ResourceNotFound("Epic Not found with id "+epicId));
        return modelMapper.map(epic, EpicDTO.class);
    }






}
