package com.kk.pethome.org.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Systemdictionarytype extends BasicPojo {
    private String sn;
    private String name;
    //通过typeId查询name
    private Systemdictionarydetail typeId;
    private Systemdictionarytype typename;
}
