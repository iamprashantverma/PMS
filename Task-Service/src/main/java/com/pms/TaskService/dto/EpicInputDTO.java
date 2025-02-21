package com.pms.TaskService.dto;


import lombok.*;

//@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EpicInputDTO extends IssueInputDTO{
    private String epicGoal;

}
