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
  mutation CreateBug($bugInput: BugInput) {
    createBug(bugInput: $bugInput) {
      id
      title
      description
      epicId
      storyId
      taskId
      status
      priority
      tag
      deadline
      projectId
      assignees
      label
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
  mutation AssignMemberToTask($taskId: ID!, $memberId: ID!) {
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
mutation AssignMemberToSubTask($taskId: ID!, $memberId: ID!) {
  assignMemberToSubTask(taskId: $taskId, memberId: $memberId) {
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

export const UNASSIGN_MEMBER_TO_TASK = gql`
mutation UnAssignMemberToTask($taskId: ID!, $memberId: ID!) {
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

export const UNASSIGN_MEMBER_TO_SUBTASK = gql`
  mutation UnAssignMemberToSubTask($taskId: ID!, $memberId: ID!) {
    unAssignMemberToSubTask(taskId: $taskId, memberId: $memberId) {
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

