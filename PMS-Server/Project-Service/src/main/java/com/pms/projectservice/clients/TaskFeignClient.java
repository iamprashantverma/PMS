package com.pms.projectservice.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "task-service",path = "/task")
public interface TaskFeignClient {
}
