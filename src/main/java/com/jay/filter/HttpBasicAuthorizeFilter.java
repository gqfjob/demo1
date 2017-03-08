package com.jay.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.vo.ResultMsg;
import com.jay.vo.ResultStatusCode;


/*
 * Filter实现简单的Http Basic 认证 
 */
//@Component
//@WebFilter(filterName = "httpBasicAuthorizedFilter", urlPatterns="/user/*")
public class HttpBasicAuthorizeFilter implements Filter {
	
	private String name ="test";
	private String password = "test";
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ResultStatusCode resultStatusCode = checkHTTPBasicAuthorize(request);
		
		if (resultStatusCode != ResultStatusCode.OK) {
			//认证不通过，则输出信息
			HttpServletResponse httpResponse = (HttpServletResponse) response;  
            httpResponse.setCharacterEncoding("UTF-8");    
            httpResponse.setContentType("application/json; charset=utf-8");   
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            ObjectMapper mapper = new ObjectMapper();
            
            ResultMsg<Object> resultMsg = new ResultMsg<Object>(false, ResultStatusCode.PERMISSION_DENIED.getErrorCode(), ResultStatusCode.PERMISSION_DENIED.getErrorMsg(), null);  
            httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
			
		}else{
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	private ResultStatusCode checkHTTPBasicAuthorize(ServletRequest request)  
    {  
        try  
        {  
            HttpServletRequest httpRequest = (HttpServletRequest)request;  
            String auth = httpRequest.getHeader("Authorization");  
            if ((auth != null) && (auth.length() > 6))  
            {  
                String HeadStr = auth.substring(0, 5).toLowerCase();  
                if (HeadStr.compareTo("basic") == 0)  
                {  
                    auth = auth.substring(6, auth.length());    
                    String decodedAuth = getFromBASE64(auth);  
                    if (decodedAuth != null)  
                    {  
                        String[] UserArray = decodedAuth.split(":");  
                          
                        if (UserArray != null && UserArray.length == 2)  
                        {  
                            if (UserArray[0].compareTo(name) == 0  
                                    && UserArray[1].compareTo(password) == 0)  
                            {  
                                return ResultStatusCode.OK;  
                            }  
                        }  
                    }  
                }  
            }  
            return ResultStatusCode.PERMISSION_DENIED;  
        }  
        catch(Exception ex)  
        {  
            return ResultStatusCode.PERMISSION_DENIED;  
        }  
          
    }  
      
    private String getFromBASE64(String s) {    
        if (s == null)    
            return null;    
        BASE64Decoder decoder = new BASE64Decoder();    
        try {    
            byte[] b = decoder.decodeBuffer(s);    
            return new String(b);    
        } catch (Exception e) {    
            return null;    
        }    
    } 

}
