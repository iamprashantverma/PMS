package com.pms.activitytrackingservice.services.impl;

import com.pms.activitytrackingservice.dto.GitHubChangeDTO;
import com.pms.activitytrackingservice.entities.GitHubChange;
import com.pms.activitytrackingservice.repositories.GitHubChangeRepository;
import com.pms.activitytrackingservice.services.GitHubChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubChangeServiceImpl implements GitHubChangeService {

    private final GitHubChangeRepository gitHubChangeRepository;
    private final ModelMapper modelMapper;

    private GitHubChange convertToEntity(GitHubChangeDTO gitHubChangeDTO) {
        return modelMapper.map(gitHubChangeDTO, GitHubChange.class);
    }

    private GitHubChangeDTO convertToDTO(GitHubChange gitHubChange) {
        return modelMapper.map(gitHubChange, GitHubChangeDTO.class);
    }

    @Override
    public void saveGitHubChange(GitHubChangeDTO gitHubChange) {
        GitHubChange entity = convertToEntity(gitHubChange);
        gitHubChangeRepository.save(entity);
    }

    @Override
    public List<GitHubChangeDTO> getAllGitHubChanges() {
        return gitHubChangeRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<GitHubChangeDTO> getChangesByRepository(String repositoryName) {
        return gitHubChangeRepository.findAllByRepositoryName(repositoryName)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<GitHubChangeDTO> getChangesByAuthor(String author) {
       List<GitHubChange> gitHubChanges = gitHubChangeRepository.findAllByAuthor(author);
       return gitHubChanges.stream()
               .map(this::convertToDTO)
               .toList();
    }



}
