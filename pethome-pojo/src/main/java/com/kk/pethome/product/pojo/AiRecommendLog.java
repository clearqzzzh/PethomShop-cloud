package com.kk.pethome.product.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendLog extends BasicPojo {
    private String sessionId;
    private String keyword;
    private String productIds;
    private String aiReply;
    private Integer userFeedback;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
