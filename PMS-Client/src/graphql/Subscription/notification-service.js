import { gql } from '@apollo/client';

export const GET_LATEST_NOTIFICATION = gql`
  subscription GetLatestNotification($userId: String!) {
    getLatestNotification(userId: $userId) {
      notificationId
      title
      userId
      entityId
      description
      createdDate
      updatedDate
      deadline
      priority
      projectId
      assignees
      oldStatus
      newStatus
      updatedBy
      eventType
      action
      isReads
      commentText
      commentedBy
    }
  }
`;
