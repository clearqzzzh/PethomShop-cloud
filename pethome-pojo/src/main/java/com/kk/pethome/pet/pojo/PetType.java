package com.kk.pethome.pet.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kk.pethome.basic.pojo.BasicPojo;
import com.kk.pethome.org.pojo.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetType extends BasicPojo {
        private String name;
        private String description;
        private String dirPath;
        private Long pid;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<PetType> children = new ArrayList<>();}
