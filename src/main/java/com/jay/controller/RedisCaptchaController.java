package com.jay.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jay.util.CookieUtils;
import com.jay.vo.ResultMsg;
import com.jay.vo.ResultStatusCode;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.gimpy.FishEyeGimpyRenderer;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/redis")
public class RedisCaptchaController {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static int captchaExpires = 3 * 60; // 超时时间3min,验证码超时，自动冲redis中删除
	private static int captchaW = 200;
	private static int captchaH = 60;
	private static String cookieName = "CaptchaCode";

	@RequestMapping(value = "getcaptcha", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] getCaptcha(HttpServletResponse response) {
		// 生成验证码
		String uuid = UUID.randomUUID().toString();
		Captcha captcha = new Captcha.Builder(captchaW, captchaH).addText()
				.addBackground(new GradiatedBackgroundProducer()).gimp(new FishEyeGimpyRenderer()).build();

		// 将验证码以<key,value>形式缓存到redis
		redisTemplate.opsForValue().set(uuid, captcha.getAnswer(), captchaExpires, TimeUnit.SECONDS);

		// 将验证码key，及验证码的图片返回
		Cookie cookie = new Cookie(cookieName, uuid);
		response.addCookie(cookie);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			ImageIO.write(captcha.getImage(), "png", bao);
			return bao.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	/*
	 * 说明：
	 *    1.captchaCode来自客户端的Cookie，在访问时，通过服务端设置
	 *    2.captcha是用户填写的验证码，将用户填写的验证码和通过captchaCode从redis中获取的验证码进行对比即可
	 * 
	 */
	@ApiOperation(value = "验证码校验")
	@RequestMapping(value = "/captcha/check/{captcha}")
	@ResponseBody
	public ResultMsg<Object> checkCaptcha(@PathVariable("captcha") String captcha, HttpServletRequest request){
		
		String captchaCode = CookieUtils.getCookie(request, cookieName);
		
		ResultMsg<Object> result;
		
		try{
		if (captcha == null)  
	    {  
	        throw new Exception();  
	    }  
		
		//redis中查询验证码
		String captchaValue = redisTemplate.opsForValue().get(captchaCode);
		
		if (captchaValue == null) {
			throw new Exception();
		}
		
		if (captchaValue.compareToIgnoreCase(captcha) != 0) {
			throw new Exception();
		}
		
		//验证码匹配成功，redis则删除对应的验证码
	    redisTemplate.delete(captchaCode);
				
		return new ResultMsg<Object>(true, ResultStatusCode.OK.getErrorCode(), ResultStatusCode.OK.getErrorMsg(), null);

		}catch (Exception e) {
			result = new ResultMsg<Object>(false, ResultStatusCode.INVALID_CAPTCHA.getErrorCode(), ResultStatusCode.INVALID_CAPTCHA.getErrorMsg(), null);
		}
		return result;
	}
	

}
