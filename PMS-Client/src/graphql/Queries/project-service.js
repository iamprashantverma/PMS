import { gql } from "@apollo/client";

export const FIND_ALL_PROJECT_BY_USER = gql`
    query FindAllProject($userId: String!, $pageNo: Int) {
      findAllProject(userId: $userId, pageNo: $pageNo) {
      projectId
      title
      description
      status
      priority
      projectCreator
    }
  }
`;
