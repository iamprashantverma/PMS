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
