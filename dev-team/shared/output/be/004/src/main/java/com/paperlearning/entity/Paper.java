package com.paperlearning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("papers")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String abstract_;
    
    private String authors;
    
    private String pdfUrl;
    
    @TableField(typeHandler = org.apache.ibatis.type.BlobTypeHandler.class)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] coverImage;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
