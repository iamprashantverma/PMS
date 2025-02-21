package com.pms.TaskService.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "project-service",path = "/project")
public interface ProjectFeignClient {

}
