import { gql } from "@apollo/client";

export const FIND_ALL_PROJECT_BY_USER = gql`
    query FindAllProject($pageNo: Int) {
      findAllProject(pageNo: $pageNo) {
      projectId
      title
      description
      status
      priority
      projectCreator
      image
    }
  }
`;

export const FIND_PROJECT_BY_ID = gql`
  query FindProjectById($projectId: String!) {
    getProject(projectId: $projectId) {
      projectId
      title
      description
      startDate
      deadline
      endDate
      createdAt
      extendedDate
      status
      image
      priority
      projectCreator
      clientId
      memberIds
      chatRoomIds
      epicIds
      taskIds
      bugIds
      notification
    }
  }
`;

export const FIND_ALL_PROJECT = gql`
  query FindAllProject {
    findAllProject {
      projectId
      title
      description
      startDate
      deadline
      endDate
      createdAt
      extendedDate
      status
      image
      priority
      projectCreator
      clientId
      memberIds
      chatRoomIds
      epicIds
      taskIds
      bugIds
      notification
    }
  }
`;

