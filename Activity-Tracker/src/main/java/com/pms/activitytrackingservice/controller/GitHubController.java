package com.pms.activitytrackingservice.controller;

import com.pms.activitytrackingservice.dto.GitHubChangeDTO;
import com.pms.activitytrackingservice.services.GitHubChangeService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling GitHub change events and processing webhook payloads.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/github")
public class GitHubController {

    private final GitHubChangeService gitHubChangeService;

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
            String repositoryName = ((Map<String, Object>) payload.get("repository")).get("full_name").toString();
            String commitMessage = ((Map<String, Object>) ((Map<String, Object>) payload.get("head_commit")).get("message")).toString();
            String commitHash = ((Map<String, Object>) payload.get("head_commit")).get("id").toString();
            String author = ((Map<String, Object>) ((Map<String, Object>) payload.get("head_commit")).get("author")).get("name").toString();

            GitHubChangeDTO change = new GitHubChangeDTO();
            change.setEventType(eventType);
            change.setRepositoryName(repositoryName);
            change.setBranch(payload.get("ref").toString().replace("refs/heads/", ""));
            change.setCommitMessage(commitMessage);
            change.setCommitHash(commitHash);
            change.setAuthor(author);

            gitHubChangeService.saveGitHubChange(change);
        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Error processing event");
        }

        return ResponseEntity.ok("Webhook event processed successfully");
    }

    @PostMapping("/hello/{hello}")
    public void getHello(@PathVariable String hello) {
        log.info("Received message: {}", hello);
    }

    /**
     * Endpoint to save a new GitHub change event manually.
     */
    @PostMapping("/save")
    public ResponseEntity<GitHubChangeDTO> saveGitHubChange(@RequestBody GitHubChangeDTO gitHubChange) {
        log.info("Saving new GitHub change: {}", gitHubChange);
        return ResponseEntity.ok(gitHubChangeService.saveGitHubChange(gitHubChange));
    }

    /**
     * Endpoint to retrieve all GitHub change events.
     */
    @GetMapping("/all")
    public ResponseEntity<List<GitHubChangeDTO>> getAllGitHubChanges() {
        log.info("Fetching all GitHub changes");
        return ResponseEntity.ok(gitHubChangeService.getAllGitHubChanges());
    }

//    /**
//     * Endpoint to retrieve GitHub change events by repository name.
//     */
//    @GetMapping("/repo/{repositoryName}")
//    public ResponseEntity<List<GitHubChangeDTO>> getChangesByRepository(@PathVariable String repositoryName) {
//        log.info("Fetching changes for repository: {}", repositoryName);
//        return ResponseEntity.ok(gitHubChangeService.getChangesByRepository(repositoryName));
//    }
//
//    /**
//     * Endpoint to retrieve GitHub change events by author.
//     */
//    @GetMapping("/author/{author}")
//    public ResponseEntity<List<GitHubChangeDTO>> getChangesByAuthor(@PathVariable String author) {
//        log.info("Fetching changes by author: {}", author);
//        return ResponseEntity.ok(gitHubChangeService.getChangesByAuthor(author));
//    }
//
//    /**
//     * Endpoint to retrieve a specific GitHub change event by its ID.
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<GitHubChangeDTO> getGitHubChangeById(@PathVariable Long id) {
//        log.info("Fetching GitHub change by ID: {}", id);
//        return ResponseEntity.ok(gitHubChangeService.getGitHubChangeById(id));
//    }
}
