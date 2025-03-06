package com.pms.activitytrackingservice.services.impl;

import com.pms.activitytrackingservice.dto.GitHubDTO;
import com.pms.activitytrackingservice.entities.GitHub;
import com.pms.activitytrackingservice.repositories.GitHubRepository;
import com.pms.activitytrackingservice.services.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubServiceImpl implements GitHubService {

    private final GitHubRepository gitHubRepository;
    private final ModelMapper modelMapper;

    private GitHub convertToEntity(GitHubDTO gitHubDTO) {
        return modelMapper.map(gitHubDTO, GitHub.class);
    }

    private GitHubDTO convertToDTO(GitHub gitHub) {
        return modelMapper.map(gitHub, GitHubDTO.class);
    }

    @Override
    public void saveGitHubChange(GitHubDTO gitHubChange) {
        GitHub entity = convertToEntity(gitHubChange);
        gitHubRepository.save(entity);
    }

    @Override
    public List<GitHubDTO> getAllGitHubChanges() {
        return gitHubRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<GitHubDTO> getChangesByRepository(String repositoryName) {
        return gitHubRepository.findAllByRepositoryName(repositoryName)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<GitHubDTO> getChangesByAuthor(String author) {
       List<GitHub> gitHubs = gitHubRepository.findAllByAuthor(author);
       return gitHubs.stream()
               .map(this::convertToDTO)
               .toList();
    }



}
