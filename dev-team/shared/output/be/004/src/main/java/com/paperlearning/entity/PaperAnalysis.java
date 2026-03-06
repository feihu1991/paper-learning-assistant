package com.paperlearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("paper_analysis")
public class PaperAnalysis {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long paperId;
    
    @Column(columnDefinition = "TEXT")
    private String background;
    
    @Column(columnDefinition = "JSON")
    private String coreConcepts;
    
    @Column(columnDefinition = "JSON")
    private String methodology;
    
    @Column(columnDefinition = "JSON")
    private String keyInnovations;
    
    @Column(columnDefinition = "JSON")
    private String learningPath;
    
    private Integer difficulty;
    
    private Integer estimatedTime;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
