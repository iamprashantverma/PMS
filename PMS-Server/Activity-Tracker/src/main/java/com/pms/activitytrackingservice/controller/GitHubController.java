package com.pms.activitytrackingservice.controller;

import com.pms.activitytrackingservice.dto.GitHubDTO;
import com.pms.activitytrackingservice.services.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling GitHub change events and processing webhook payloads.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/github")
public class GitHubController {

    private final GitHubService gitHubService;

    /**
     * GitHub Webhook Endpoint to receive and process GitHub events.
     *
     * @param payload The event data sent by GitHub.
     * @param eventType The GitHub event type header.
     * @return ResponseEntity confirming webhook processing.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleGitHubWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("X-GitHub-Event") String eventType) {

        log.info("Received GitHub Webhook - Event: {}", eventType);

        try {
            // Extract repository information
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            String repositoryName = Optional.ofNullable(repository)
                    .map(repo -> repo.get("full_name").toString().split("/"))
                    .map(parts -> parts[parts.length - 1]) // Extract the last part (actual repo name)
                    .orElse("Unknown");

            // Extract commit information
            Map<String, Object> headCommit = (Map<String, Object>) payload.get("head_commit");
            String commitMessage = headCommit != null ? headCommit.get("message").toString() : "";
            String commitHash = headCommit != null ? headCommit.get("id").toString() : "";
            Map<String, Object> author = headCommit != null ? (Map<String, Object>) headCommit.get("author") : null;
            String authorName = author != null ? author.get("name").toString() : "";

            // Extract branch information
            String ref = payload.get("ref") != null ? payload.get("ref").toString() : "";
            String branch = ref.replace("refs/heads/", "");

            // Create and populate the GitHubChangeDTO object
            GitHubDTO change = new GitHubDTO();
            change.setEventType(eventType);
            change.setRepositoryName(repositoryName);
            change.setBranch(branch);
            change.setCommitMessage(commitMessage);
            change.setCommitHash(commitHash);
            change.setAuthor(authorName);

            // Save the change in your service
            gitHubService.saveGitHubChange(change);

        } catch (Exception e) {
            // Log error and return bad request
            log.error("Error processing webhook event: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error processing event");
        }

        // Return success response
        return ResponseEntity.ok("Webhook event processed successfully");
    }


    @PostMapping("/hello/{hello}")
    public void getHello(@PathVariable String hello) {
        log.info("Received message: {}", hello);
    }

    /**
     * Endpoint to retrieve all GitHub change events.
     */
    @GetMapping("/all")
    public ResponseEntity<List<GitHubDTO>> getAllGitHubChanges() {
        log.info("Fetching all GitHub changes");
        return ResponseEntity.ok(gitHubService.getAllGitHubChanges());
    }

    /**
     * Endpoint to retrieve GitHub change events by repository name.
     */
    @GetMapping("/repo/{repositoryName}")
    public ResponseEntity<List<GitHubDTO>> getChangesByRepository(@PathVariable String repositoryName) {
        log.info("Fetching changes for repository: {}", repositoryName);
        return ResponseEntity.ok(gitHubService.getChangesByRepository(repositoryName));
    }

    /**
     * Endpoint to retrieve GitHub change events by author.
     */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<GitHubDTO>> getChangesByAuthor(@PathVariable String author) {
        log.info("Fetching changes by author: {}", author);
        return ResponseEntity.ok(gitHubService.getChangesByAuthor(author));
    }


}
