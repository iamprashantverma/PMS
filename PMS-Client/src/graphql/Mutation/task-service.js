import { gql } from '@apollo/client';

export const CREATE_EPIC = gql`
  mutation CreateEpic($epic: EpicInput!, $image: Upload) {
    createEpic(epic: $epic, image: $image) {
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
    }
  }
`;

export const CREATE_BUG = gql`
  mutation CreateBug($bugInput: BugInput!, $image: Upload) {
    createBug(bugInput: $bugInput, image: $image) {
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
      image
      projectId
      assignees
      label
      expectedOutcome
      actualOutcome
    }
  }
`;

export const CREATE_STORY = gql`
  mutation CreateStory($story: StoryInput!, $image: Upload) {
    createStory(story: $story, image: $image) {
      title
      status
      priority
      deadline
      label
      projectId
      reporter
      createdAt
    }
  }
`;

 export const CREATE_TASK = gql`
  mutation CreateTask($task: TaskInput!, $image:Upload) {
    createTask(task: $task, image: $image) {
      id
      title
      description
      project
      assignees
      status
      priority
      label
      memberId
      deadline
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
      deadline
    }
  }
`;

export const ASSIGN_MEMBER_TO_SUBTASK = gql`
mutation AssignMemberToSubTask($taskId: String!, $memberId: String!) {
  assignMemberToSubTask(taskId: $taskId, memberId: $memberId) {
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

export const UNASSIGN_MEMBER_TO_TASK = gql`
mutation UnAssignMemberToTask($taskId: String!, $memberId: String!) {
  unAssignMemberToTask(taskId: $taskId, memberId: $memberId) {
    id
    title
    description
    project
    assignees
    updatedAt
    status
    priority
    label
    deadline
  }
}
`;

export const CHANGE_SUBTASK_STATUS = gql`
  mutation ChangeSubTaskStatus($subTaskId: String!, $status: IssueStatus!) {
    changeSubTaskStatus(subTaskId: $subTaskId, status: $status) {
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

export const UNASSIGN_MEMBER_TO_SUBTASK = gql`
  mutation UnAssignMemberToSubTask($taskId: String!, $memberId: String!) {
    unAssignMemberToSubTask(taskId: $taskId, memberId: $memberId) {
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

export const DELETE_TASK = gql`
  mutation DeleteTask($taskId: ID!) {
    deleteTask(taskId: $taskId) {
      message
    }
  }
`;

export const DELETE_BUG = gql`
  mutation DeleteBug($bugId: ID!) {
    deleteBug(bugId: $bugId) {
      message
    }
  }
`;

export const ADD_COMMENT = gql`
  mutation AddComment($taskId: String!, $userId: String!, $message: String!) {
    addComment(commentDTO: { taskId: $taskId, message: $message, userId: $userId }) {
      taskId
      userId
      message
    }
  }
`;

export const DELETE_COMMENT = gql`
  mutation DeleteComment($commentId: Int!) {
    deleteComment(commentId: $commentId) 
  }
`;

export const CHANGE_BUG_STATUS = gql`
  mutation ChangeBugStatus($bugId: String!, $status: IssueStatus!) {
    changeBugStatus(bugId: $bugId, status: $status) {
      message
    }
  }
`;

export const CHANGE_STATUS = gql`
  mutation ChangeStatus($subTaskId: ID!, $status: String!) {
    changeStatus(subTaskId: $subTaskId, status: $status) {
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

export const CHANGE_TASK_STATUS = gql`
  mutation ChangeTaskStatus($taskId: String!, $status: IssueStatus!) {
    changeTaskStatus(taskId: $taskId, status: $status) {
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

export const ASSIGN_BUG_TO_USER = gql`
  mutation AssignBugToUser($bugId: String!, $userId: String!) {
    assignBugToUser(bugId: $bugId, userId: $userId) {
      message
    }
  }
`;

export const CREATE_SUBTASK = gql`
  mutation CreateSubTask($subTask: SubTaskInput!) {
    createSubTask(subTask: $subTask) {
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