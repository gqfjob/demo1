package com.jay.vo;
/*
 * 相应结果枚举类
 */
public enum ResultStatusCode {
	OK(0, "OK"),
	SYSTEM_ERR(30001, "System Error"),
	PERMISSION_DENIED(30002, "Permission Denied"),
	INVALID_CLIENTID(30003, "Invalid clientid"),
	INVALID_PASSWORD(30004, "User name or password is incorrect"),
	INVALID_CAPTCHA(30005, "Invalid captcha or captcha overdue"),
	INVALID_TOKEN(30006, "Invalid token");
	private int errorCode;
	private String errorMsg;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	private ResultStatusCode(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
}
