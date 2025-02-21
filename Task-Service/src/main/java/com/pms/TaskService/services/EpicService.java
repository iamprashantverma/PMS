package com.pms.TaskService.services;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.EpicInputDTO;
import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.dto.ResponseDTO;

public interface EpicService {

    boolean isExist(String epicId);

    EpicDTO createEpic(EpicInputDTO epic);

    EpicDTO updateEpic(String epicId, EpicInputDTO epic);

    EpicDTO deleteEpic(String epicId);

    EpicDTO getEpicById(String epicId);

//    EpicDTO getAllEpic();



//    changeEpicStatus – Update the status of an epic (e.g., To Do, In Progress, Done).

//transitionEpic – Move an epic through a workflow.
//archiveEpic – Archive an epic when it's completed.

//    getEpicProgress – Retrieve progress details of an epic (number of completed vs. remaining tasks).

}
