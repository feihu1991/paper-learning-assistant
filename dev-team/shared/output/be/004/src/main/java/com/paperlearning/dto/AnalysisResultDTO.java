package com.paperlearning.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalysisResultDTO {
    private Long id;
    private Long paperId;
    private String background;
    private List<String> coreConcepts;
    private List<String> methodology;
    private List<String> keyInnovations;
    private List<String> learningPath;
    private Integer difficulty;
    private Integer estimatedTime;
    private String status;
}
