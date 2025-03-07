package com.pms.projectservice.event.enums;

public enum Actions {
    /** New entity created */
    CREATED,

    /** Entity updated */
    UPDATED,

    /** Entity deleted */
    DELETED,

    /** Status changed (e.g., Open → In Progress) */
    STATUS_CHANGED,

    /** Assigned to a user */
    ASSIGNED,

    /** Unassigned from a user */
    UNASSIGNED,

    /** Comment added */
    COMMENTED,

    /** File/attachment added */
    ATTACHMENT_ADDED,

    /** File/attachment removed */
    ATTACHMENT_REMOVED,

    /** Priority changed (e.g., High → Medium) */
    PRIORITY_CHANGED,

    /** Tag added to an entity */
    TAG_ADDED,

    /** Tag removed from an entity */
    TAG_REMOVED,

    /** Entity linked to another (e.g., Story linked to an Epic) */
    LINKED,

    /** Entity unlinked from another */
    UNLINKED,

    /** Bug or Issue resolved */
    RESOLVED,

    /** Bug or Issue reopened */
    REOPENED,

    /** Entity closed (e.g., Task Completed, Bug Closed) */
    CLOSED,

}
