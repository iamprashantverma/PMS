package com.pms.projectservice.resolver;

import com.pms.projectservice.dto.ProjectDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;


@Component
public class ProjectResolver {

    @QueryMapping
    public ProjectDTO getProject(@Argument("projectId") String projectId){
        return new ProjectDTO();
    }

}
