package com.jay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jay.bean.UserInfo;
import com.jay.dao.UserInfoRepository;
import com.jay.properties.JwtInfo;
import com.jay.util.Md5SaltUtil;
import com.jay.util.jwt.AccessToken;
import com.jay.util.jwt.JwtHelper;
import com.jay.vo.LoginParam;
import com.jay.vo.ResultMsg;
import com.jay.vo.ResultStatusCode;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/jwt")
public class JwtWebTokenController {

	@Autowired
	private JwtInfo jwtInfo;

	@Autowired
	private UserInfoRepository userDao;
	
	@ApiOperation(value="输出自定义的Properties配置解析类内容")
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public Object getJwtInfo() {
		return new ResultMsg<JwtInfo>(true, ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), jwtInfo);
	}
	
	@ApiOperation(value = "使用JWT进行token验证，传入登录参数")
	@RequestMapping(value = "oauth/token", method = RequestMethod.POST)  
    public Object getAccessToken(@RequestBody LoginParam loginParam)  
    {  
        ResultMsg<Object> resultMsg;  
        try  
        {  
            if(loginParam.getClientId() == null   
                    || (loginParam.getClientId().compareTo(jwtInfo.getClientId()) != 0))  
            {  
                resultMsg = new ResultMsg<Object>(false, ResultStatusCode.INVALID_CLIENTID.getErrorCode(),   
                        ResultStatusCode.INVALID_CLIENTID.getErrorMsg(), null);  
                return resultMsg;  
            }  
              
            //验证码校验在后面章节添加  
              
              
            //验证用户名密码  
            UserInfo user = userDao.findUserInfoByName(loginParam.getUserName());  
            if (user == null)  
            {  
                resultMsg = new ResultMsg<Object>(false, ResultStatusCode.INVALID_PASSWORD.getErrorCode(),  
                        ResultStatusCode.INVALID_PASSWORD.getErrorMsg(), null);  
                return resultMsg;  
            }  
            else  
            {  
                String md5Password = Md5SaltUtil.md5Hex(loginParam.getPassword()+user.getSalt());  
                  
                if (md5Password.compareTo(user.getPassword()) != 0)  
                {  
                    resultMsg = new ResultMsg<Object>(false, ResultStatusCode.INVALID_PASSWORD.getErrorCode(),  
                            ResultStatusCode.INVALID_PASSWORD.getErrorMsg(), null);  
                    return resultMsg;  
                }  
            }  
              
            //拼装accessToken  
            String accessToken = JwtHelper.createJWT(loginParam.getUserName(), String.valueOf(user.getName()),  
                    user.getRole(), jwtInfo.getClientId(), jwtInfo.getName(),  
                    jwtInfo.getExpiresSecond() * 1000, jwtInfo.getBase64Secret());  
              
            //返回accessToken  
            AccessToken accessTokenEntity = new AccessToken();  
            accessTokenEntity.setAccess_token(accessToken);  
            accessTokenEntity.setExpires_in(jwtInfo.getExpiresSecond());  
            accessTokenEntity.setToken_type("bearer");  
            resultMsg = new ResultMsg<Object>(true, ResultStatusCode.OK.getErrorCode(),   
                    ResultStatusCode.OK.getErrorMsg(), accessTokenEntity);  
            return resultMsg;  
              
        }  
        catch(Exception ex)  
        {  
            resultMsg = new ResultMsg<Object>(false, ResultStatusCode.SYSTEM_ERR.getErrorCode(),   
                    ResultStatusCode.SYSTEM_ERR.getErrorMsg(), null);  
            return resultMsg;  
        }  
    }  
	
}
