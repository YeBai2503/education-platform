package com.baymax.exam.user.feign;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.user.model.Courses;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.user.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2022/10/9 19:30
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "exam-user",contextId = "UserServiceClient",path = "/user")
public interface UserClient {
    @GetMapping("/findUser")
     User findUser(@RequestParam String username);
    @GetMapping("/base/info")
    Result<User> getBaseUserInfo();
    @GetMapping("/{userId}/info")
    User getBaseUserInfoById(@PathVariable Integer userId);
    @PostMapping ("/batchUser")
    List<User> getBatchUser(@RequestBody Collection<Integer> ids);
    
    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/inner/{userId}")
    Result<User> getUserById(@PathVariable Integer userId);
}
