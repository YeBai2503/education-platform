package com.baymax.exam;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;


@Data
public class VueRouteLocationRaw {
    String name;
    String path;
    Map<String,Object> params;
    Map<String,Object> query;
    @JsonIgnore
    public String getJson(){
        return JSONUtil.toJsonStr(this);
    }
}
