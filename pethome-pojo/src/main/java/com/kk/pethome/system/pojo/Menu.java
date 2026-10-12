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
public class Menu extends BasicPojo {
    private String name;
    private String component;
    private String url;
    private String icon;
    private Integer index;
    private Long parentId;
    private String intro;
    private String state;
    private List<Menu> children = new ArrayList<>();
}
