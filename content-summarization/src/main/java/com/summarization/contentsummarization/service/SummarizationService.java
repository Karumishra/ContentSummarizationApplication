package com.summarization.contentsummarization.service;

import com.summarization.contentsummarization.dto.SummarizationResponseDTO;
import org.springframework.web.multipart.MultipartFile;


public interface SummarizationService {
    public SummarizationResponseDTO summarizeContent(MultipartFile file);
}
