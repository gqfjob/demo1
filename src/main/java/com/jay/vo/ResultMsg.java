package com.jay.vo;

/*
 * 通用的系统间交互类
 */
public class ResultMsg<T> {
	private boolean status;
	private int code;
	private String message;
	private T data;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ResultMsg() {
		super();
	}

	public ResultMsg(boolean status, int code, String message, T data) {
		super();
		this.status = status;
		this.code = code;
		this.message = message;
		this.data = data;
	}

}
