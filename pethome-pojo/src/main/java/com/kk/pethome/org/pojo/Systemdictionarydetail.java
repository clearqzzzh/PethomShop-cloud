package com.kk.pethome.org.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Systemdictionarydetail extends BasicPojo {
    private String name;
    private Long typesId;
    private Systemdictionarytype type;
}
