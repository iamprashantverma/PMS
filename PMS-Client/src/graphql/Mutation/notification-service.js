import { gql } from '@apollo/client';

export const READ_SINGLE_NOTIFICATION = gql`
  mutation ReadSingleNotification($id: Int!) {
    readSingleNotification(id: $id) {
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

export const READ_ALL_NOTIFICATION = gql`
  mutation ReadAllNotification($userId: String!) {
    readAllNotification(userId: $userId) {
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
