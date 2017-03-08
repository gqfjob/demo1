package com.jay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jay.bean.UserInfo;
import com.jay.dao.UserInfoRepository;
import com.jay.vo.ResultMsg;
import com.jay.vo.ResultStatusCode;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired  
    private UserInfoRepository userRepositoy;  
      
    @RequestMapping(value = "getuser", method = RequestMethod.GET)  
    public Object getUser(@RequestParam("id") int id)  
    {  
        UserInfo userEntity = userRepositoy.findUserInfoById(id);  
        ResultMsg<UserInfo> resultMsg = new ResultMsg<UserInfo>(true, ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), userEntity);  
        return resultMsg;  
    }  
      
    @RequestMapping(value = "getusers", method = RequestMethod.GET)  
    public Object getUsers(String role)  
    {  
        List<UserInfo> userEntities = userRepositoy.findUserInfoByRole(role);  
        ResultMsg<List> resultMsg = new ResultMsg<List>(true,ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), userEntities);  
        return resultMsg;  
    }  
      
    @Modifying  
    @RequestMapping(value = "adduser", method = RequestMethod.POST)  
    public Object addUser(@RequestBody UserInfo userEntity)  
    {  
        userRepositoy.save(userEntity);  
        ResultMsg<UserInfo> resultMsg = new ResultMsg<UserInfo>(true, ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), userEntity);  
        return resultMsg;  
    }  
      
    @Modifying  
    @RequestMapping(value = "updateuser", method = RequestMethod.PUT)  
    public Object updateUser(@RequestBody UserInfo userEntity)  
    {  
        UserInfo user = userRepositoy.findUserInfoById(userEntity.getId());  
        if (user != null)  
        {  
            user.setName(userEntity.getName());  
            userRepositoy.save(user);  
        }  
        ResultMsg<UserInfo> resultMsg = new ResultMsg<UserInfo>(true, ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), user);  
        return resultMsg;  
    }  
      
    @Modifying  
    @RequestMapping(value = "deleteuser", method = RequestMethod.DELETE)  
    public Object deleteUser(int id)  
    {  
        userRepositoy.delete(id);  
        ResultMsg<String> resultMsg = new ResultMsg<String>(true, ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), "成功");  
        return resultMsg;  
    }  
}
