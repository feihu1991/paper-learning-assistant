package com.paperlearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("analysis_tasks")
public class AnalysisTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long paperId;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
