import { gql } from '@apollo/client';

export const GET_ALL_EPICS = gql`
  query GetAllEpics {
    getAllEpics {
      id
      title
      description
      project
      assignees
      creator
      createdDate
      updatedDate
      deadline
      status
      priority
      tag
      epicGoal
    }
  }
`;

export const GET_ALL_TASKS_BY_PROJECT_ID = gql`
  query GetAllTaskByProjectId($projectId: ID!) {
    getAllTaskByProjectId(projectId: $projectId) {
      id
      title
      description
      project
      assignees
      updatedDate
      status
      priority
      label
      isBlocking
      memberId
      deadline
    }
  }
`;

export const GET_ALL_SUBTASKS = gql`
  query GetAllSubTasks {
    getAllSubTasks {
      id
      title
      description
      assignees
      creator
      createdDate
      updatedDate
      status
      priority
      label
    }
  }
`;

export const GET_ALL_STORIES_BY_PROJECT_ID = gql`
  query GetAllStoriesByProjectId($projectId: String!) {
    getAllStoriesByProjectId(projectId: $projectId) {
      id
      title
      description
      project
      assignees
      creator
      createdAt
      deadline
      status
      priority
      label
    }
  }
`;

export const GET_STORY_BY_ID = gql`
  query GetStoryById($storyId: ID!) {
    getStoryById(storyId: $storyId) {
      id
      title
      description
      project
      assignees
      creator
      createdDate
      updatedDate
      deadline
      status
      priority
      label
      acceptanceCriteria
    }
  }
`;

export const GET_SUBTASK_BY_ID = gql`
  query GetSubTaskById($subTaskId: ID!) {
    getSubTaskById(subTaskId: $subTaskId) {
      id
      title
      description
      assignees
      creator
      createdDate
      updatedDate
      status
      priority
      label
    }
  }
`;

export const GET_TASK_BY_ID = gql`
  query GetTaskById($taskId: ID!) {
    getTaskById(taskId: $taskId) {
      id
      title
      description
      project
      assignees
      updatedDate
      status
      priority
      label
      isBlocking
      memberId
      deadline
    }
  }
`;

export const GET_TASKS_BY_STATUS = gql`
  query GetTasksByStatus($status: String!) {
    getTasksByStatus(status: $status) {
      id
      title
      description
      project
      assignees
      updatedDate
      status
      priority
      label
      isBlocking
      memberId
      deadline
    }
  }
`;

export const GET_ALL_EPICS_BY_PROJECT_ID = gql`
  query GetAllEpicsByProjectId($projectId: String!) {
    getAllEpicsByProjectId(projectId: $projectId) {
      id
      title
      description
      project
      assignees
      createdDate
      creator
      updatedDate
      deadline
      status
      priority
      epicGoal
    }
  }
`;

