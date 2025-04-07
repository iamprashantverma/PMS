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
      updatedAt
      status
      priority
      label
      memberId
      deadline
      epicId
      storyId
      reporter
      createdAt
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
      createdAt
      updatedAt
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
      updatedAt
      status
      priority
      label
      memberId
      deadline
      epicId
      storyId
      reporter
      createdAt
      subTasks {
            id
            title
            description
            assignees
            creator
            createdAt
            updatedAt
            status
            priority
            label
        }
    }
  }
`;

export const GET_TASKS_BY_STATUS_AND_EPIC = gql`
  query GetTasksByStatusAndEpic($status: IssueStatus, $epicId: String!) {
    getTasksByStatusAndEpic(status: $status, epicId: $epicId) {
      id
      title
      description
      project
      assignees
      updatedAt
      priority
      status
      memberId
      deadline
      label
      createdAt
    }
  }
`;

export const ASSIGN_MEMBER_TO_TASK = gql`
    mutation AssignMemberToTask($taskId: String!, $memberId: String!) {
        assignMemberToTask(taskId: $taskId, memberId: $memberId) {
            id
            title
            description
            project
            assignees
            updatedAt
            status
            priority
            label
            memberId
            deadline
        }
    }
`;

export const GET_ASSIGNED_MEMBERS = gql`
    query GetAssignedMembers($epicId: String!) {
        getAssignedMembers(epicId: $epicId) {
            name
            email
            image
            userId
        }
    }
`;


export const GET_BUGS_BY_PROJECT_ID = gql`
  query GetBugsByProjectId($projectId: String!) {
    getBugsByProjectId(projectId: $projectId) {
      id
      title
      description
      epicId
      storyId
      taskId
      status
      priority
      tag
      createdAt
      updatedAt
      deadline
      projectId
      assignees
      label
    }
  }
`;

export const GET_BUGS_BY_USER_ID = gql`
  query GetBugsByUserId($userId: ID!) {
    getBugsByUserId(userId: $userId) {
      id
      title
      description
      epicId
      storyId
      taskId
      status
      priority
      tag
      createdDate
      updatedDate
      deadline
      projectId
      assignees
      label
    }
  }
`;

export const GET_SUBTASKS_BY_TASK_ID = gql`
  query GetSubTasksByTaskId($taskId: ID!) {
    getSubTasksByTaskId(taskId: $taskId) {
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

export const GET_TASKS_BY_USER_ID = gql`
  query GetTasksByUserId($userId: ID!) {
    getTasksByUserId(userId: $userId) {
      id
      title
      description
      project
      assignees
      updatedAt
      status
      priority
      label
      memberId
      deadline
    }
  }
`;

export const GET_EPIC_BY_ID = gql`
  query GetEpicById($epicId: String!) {
    getEpicById(epicId: $epicId) {
      id
      title
      description
      project
      creator
      assignees
      createdDate
      updatedDate
      deadline
      status
      priority
      tag
      epicGoal
      label
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
      creator
      createdDate
      updatedDate
      deadline
      status
      priority
      tag
      epicGoal
      label
      tasks {
        id
        title
        description
        project
        assignees
        updatedAt
        createdAt
        status
        priority
        label
        memberId
        deadline
        epicId
        storyId
        reporter
      }
    }
  }
`;

export const GET_BUG_BY_ID = gql`
  query GetBugById($bugId: String!) {
    getBugById(bugId: $bugId) {
      id
      title
      description
      epicId
      storyId
      taskId
      status
      priority
      tag
      createdAt
      updatedAt
      deadline
      projectId
      assignees
      label
      expectedOutcome
      actualOutcome
      image
    }
  }
`;

export const GET_SUBTASKS_ASSIGNED_TO_USER = gql`
  query GetSubTasksAssignedToUser($userId: String!) {
    getSubTasksAssignedToUser(userId: $userId) {
      id
      title
      description
      assignees
      creator
      createdAt
      updatedAt
      status
      priority
      label
    }
  }
`;

export const GET_TASKS_ASSIGNED_TO_USER = gql`
query GetTasksAssignedToUser($userId: String) {
  getTasksAssignedToUser(userId: $userId) {
    id
    title
    description
    project
    assignees
    updatedAt
    createdAt
    status
    priority
    label
    memberId
    deadline
    epicId
    storyId
    reporter
  }
}
`;

