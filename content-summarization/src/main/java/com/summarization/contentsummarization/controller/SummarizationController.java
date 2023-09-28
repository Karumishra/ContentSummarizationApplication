package com.summarization.contentsummarization.controller;

import com.summarization.contentsummarization.dto.SummarizationResponseDTO;
import com.summarization.contentsummarization.service.SummarizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api")
@CrossOrigin
public class SummarizationController {
    @Autowired
    private SummarizationService summarizationService;

    @PostMapping("/summarizeInputFile")
    public ResponseEntity<SummarizationResponseDTO> summarizeContent(@RequestParam("inputFile") MultipartFile inputFile)
    {
           SummarizationResponseDTO summary= this.summarizationService.summarizeContent(inputFile);
           return ResponseEntity.ok(summary);
    }

}
