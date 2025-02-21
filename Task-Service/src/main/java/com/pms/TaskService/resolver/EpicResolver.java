package com.pms.TaskService.resolver;


import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.EpicInputDTO;
import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.services.EpicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

//@Component
@Slf4j
@Controller
@RequiredArgsConstructor
public class EpicResolver {

    private final EpicService epicService;

    /* Creating epic */
    @MutationMapping("createEpic")
    public EpicDTO createEpic(@Argument("epic") EpicInputDTO epicInputDTO){
        return epicService.createEpic(epicInputDTO);
    }

    /* Deleting epic*/
    @MutationMapping("deleteEpic")
    public EpicDTO deleteEpic(@Argument String epicId){
        return epicService.deleteEpic(epicId);
    }

    /* find epic by id */
    @QueryMapping("getEpicById")
    public EpicDTO getEpicById(@Argument String epicId){
        return epicService.getEpicById(epicId);
    }



}
