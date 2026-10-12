package com.kk.pethome.system.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class permission extends BasicPojo {
    private String name;
    private String url;
    private String descs;
    private String sn;
}
