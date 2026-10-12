package com.kk.pethome.system.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BasicPojo {
    private String name;
    private String sn;
}
