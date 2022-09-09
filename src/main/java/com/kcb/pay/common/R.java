package com.kcb.pay.common;


import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO 数据返回封装类
 *
 * @author crazypenguin
 * @version 1.0.0
 * @createdate 2019/1/2
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public R() {

	}

	public static R error() {
		return error(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), msg);
	}


	public static R error(String code, String msg) {
		R r = new R();
		r.put("resultCode", code);
		r.put("resultMsg", msg);
		return r;
	}

	public R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok(String code, String msg) {
		R r = new R();
		r.put("resultCode", code);
		r.put("resultMsg", msg);
		return r;
	}

	public static R ok() {
		R r = new R();
		r.put("resultCode", "0");
		r.put("resultMsg", "成功");
		return r;
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}


}
