package com.pms.activitytrackingservice.services.impl;

import com.pms.activitytrackingservice.dto.GitHubChangeDTO;
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

    @Override
    public GitHubChangeDTO saveGitHubChange(GitHubChangeDTO gitHubChange) {
        System.out.println(gitHubChange.getAuthor());
        System.out.println(gitHubChange.getBranch());
        System.out.println(gitHubChange.getCommitMessage());

        return null;
    }

    @Override
    public List<GitHubChangeDTO> getAllGitHubChanges() {
        return List.of();
    }
}
