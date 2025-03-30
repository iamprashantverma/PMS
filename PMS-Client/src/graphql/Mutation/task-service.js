import { gql } from '@apollo/client';
export const CREATE_EPIC = gql`
  mutation CreateEpic($epic: EpicInput!) {
    createEpic(epic: $epic) {
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



export const CREATE_BUG = gql`
  mutation CreateBug($bugInput: BugInput!) {
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
      createdDate
      updatedDate
      deadline
      projectId
      assignees
      label
    }
  }
`;



export const CREATE_STORY = gql`
  mutation CreateStory($story: StoryInput!) {
    createStory(story: $story) {
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




export const CREATE_TASK = gql`
  mutation CreateTask($task: TaskInput!) {
    createTask(task: $task) {
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

