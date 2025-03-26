// src/graphql/Mutation/project-service.js
import { gql } from '@apollo/client';

export const UPDATE_PROJECT_DETAILS = gql`
  mutation UpdateProjectDetails($project: ProjectInput!) {
    updateProjectDetails(project: $project) {
      title
      startDate
      description
      priority
      status
      clientId
    }
  }
`;

export const REMOVE_USER_FROM_PROJECT = gql`
  mutation RemoveUserFromProject($projectId: String!, $memberId: String!) {
    removeUserFromProject(projectId: $projectId, memberId: $memberId) {
      memberIds
    }
  }
`;

export const ADD_MEMBERS_TO_PROJECT = gql`
  mutation AddMembersToProject($projectId: String!, $members: [String!]!) {
    addMembersToProject(projectId: $projectId, members: $members) {
      memberIds
    }
  }
`;

export const CREATE_PROJECT = gql`
  mutation CreateProject($project: ProjectInput!) {
    createProject(project: $project) {
      projectId
      title
      description
      startDate
      deadline
      endDate
      createdAt
      extendedDate
      status
      priority
      projectCreator
      clientId
    }
  }
`;
