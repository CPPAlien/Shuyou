package com.shuyou.net;

import java.util.List;

import com.alibaba.fastjson.JSON;

public class JsonParser {

	public static List<?> parseList(String str, Class<?> c) {
		return JSON.parseArray(str, c);
	}

	public static Object parseObject(String str, Class<?> c) {
		return JSON.parseObject(str, c);
	}

	public static String toJsonString(Object object)
	{
		return JSON.toJSONString(object);
	}
}
