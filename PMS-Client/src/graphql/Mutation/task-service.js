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

