import { gql } from "@apollo/client";

export const SUBSCRIBE_TO_COMMENT_UPDATES = gql`
  subscription SubscribeToCommentUpdates($taskId: String!) {
    subscribeToCommentUpdates(taskId: $taskId) {
      taskId
      userId
      message
      commentId
    }
  }
`;
