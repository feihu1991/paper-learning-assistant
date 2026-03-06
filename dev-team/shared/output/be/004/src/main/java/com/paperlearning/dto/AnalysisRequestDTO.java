package com.paperlearning.dto;

import lombok.Data;

@Data
public class AnalysisRequestDTO {
    private String background;
    private String[] coreConcepts;
    private String[] methodology;
    private String[] keyInnovations;
    private String[] learningPath;
    private Integer difficulty;
    private Integer estimatedTime;
}
