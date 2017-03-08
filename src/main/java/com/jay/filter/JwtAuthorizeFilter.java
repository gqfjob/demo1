package com.jay.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.properties.JwtInfo;
import com.jay.util.jwt.JwtHelper;
import com.jay.vo.ResultMsg;
import com.jay.vo.ResultStatusCode;

/*
 * 用于JWT认证的过滤器
 */
public class JwtAuthorizeFilter implements Filter{
	
	/*
	 * 注入配置文件类
	 */
	@Autowired
	private JwtInfo jwtInfo;

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		 ResultMsg<Object> resultMsg;  
	        HttpServletRequest httpRequest = (HttpServletRequest)request;  
	        String auth = httpRequest.getHeader("Authorization");  
	        if ((auth != null) && (auth.length() > 7))  
	        {  
	            String HeadStr = auth.substring(0, 6).toLowerCase();  
	            if (HeadStr.compareTo("bearer") == 0)  
	            {  
	                  
	                auth = auth.substring(7, auth.length());   
	                if (JwtHelper.parseJWT(auth, jwtInfo.getBase64Secret()) != null)  
	                {  
	                    chain.doFilter(request, response);  
	                    return;  
	                }  
	            }  
	        }  
	        
	        //验证不通过
	        HttpServletResponse httpResponse = (HttpServletResponse) response;  
	        httpResponse.setCharacterEncoding("UTF-8");    
	        httpResponse.setContentType("application/json; charset=utf-8");   
	        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
	  
	        //将验证不通过的错误返回
	        ObjectMapper mapper = new ObjectMapper();  
	          
	        resultMsg = new ResultMsg<Object>(true, ResultStatusCode.INVALID_TOKEN.getErrorCode(), ResultStatusCode.INVALID_TOKEN.getErrorMsg(), null);  
	        httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));  
	        return;  
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

}
