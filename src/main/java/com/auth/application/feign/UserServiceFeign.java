package com.auth.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.auth.application.dto.LoginCredentials;
import com.auth.application.feign.dto.AddUserRequest;
import com.auth.application.feign.dto.UserDTO;

@FeignClient(name = "user-service", url = "http://localhost:9090/")
public interface UserServiceFeign {

    @RequestMapping(method = RequestMethod.POST, value = "/user/get")
    public UserDTO getUser(@RequestBody LoginCredentials loginCredentials);

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public UserDTO addUser(@RequestBody AddUserRequest request);
}
