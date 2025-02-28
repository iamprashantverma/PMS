package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.services.EpicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EpicResolver {

    private final EpicService epicService;

    /* Creating epic */
    @MutationMapping
    public EpicDTO createEpic(@Argument EpicDTO epicDTO) {
        return epicService.createEpic(epicDTO);
    }

    /* Deleting epic */
    @MutationMapping
    public EpicDTO deleteEpic(@Argument String epicId) {
        return epicService.deleteEpic(epicId);
    }

    /* Find epic by id */
    @QueryMapping
    public EpicDTO getEpicById(@Argument String epicId) {
        return epicService.getEpicById(epicId);
    }

    /* Get all active epics */
    @QueryMapping
    public List<EpicDTO> getAllEpics() {
        return epicService.getAllActiveEpics();
    }
}
