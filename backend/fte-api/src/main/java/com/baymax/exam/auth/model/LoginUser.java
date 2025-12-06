package com.baymax.exam.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//登录用户信息
 
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
        private Integer id;
        private String username;
        private String password;
        private Boolean enabled;
        private String clientId;
        private List<String> roles;
}
