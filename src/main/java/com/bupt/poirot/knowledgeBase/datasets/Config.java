package com.bupt.poirot.knowledgeBase.datasets;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public class Config {
	public enum Env {
		DEV, TEST, STAGE, SMOKE, PROD, PROD_IAD
	}
	static Env _env;

	public static Properties prop = null;

	public static int getInteger(String key) {
		String str = getString(key);
		if (str.isEmpty()) return -1;
		return Integer.parseInt(str);
	}

	public static float getFloat(String key) {
		String str = getString(key);
		if (str.isEmpty()) return 0.0f;
		return Float.parseFloat(str);
	}

	public static boolean getBoolean(String key) {
		return "true".equals(getString(key));
	}

	public static Env getEnv() {
		if (prop == null) _init();
		return _env;
	}

	public static boolean isStageOrProd() {
		if (prop == null) _init();
		return _env.equals(Env.STAGE) || _env.equals(Env.PROD) || _env.equals(Env.PROD_IAD);
	}

	public static boolean isIAD() {
		if (prop == null) _init();
		return _env.equals(Env.PROD_IAD);
	}

	public static boolean isEnv(Env env) {
		if (prop == null) _init();
		return _env.equals(env);
	}

	/*
	 * env fallback rules:
	 * DEV -> PROD
	 * TEST -> STAGE -> PROD
	 * PROD_IAD -> PROD
	 * SMOKE -> PROD
	 */
	public static String getString(String key) {
		if (prop == null) _init();
		LinkedList<String> keys = new LinkedList<String>();
		keys.push(key);
		if (isEnv(Env.DEV)) {
			keys.push(key + ".dev");
		} else if (isEnv(Env.TEST) || isEnv(Env.STAGE)) {
			keys.push(key + ".stage");
			if (isEnv(Env.TEST)) keys.push(key + ".test");
		} else if (isEnv(Env.SMOKE)) {
			keys.push(key + ".smoke");
		} else if (isEnv(Env.PROD_IAD)) {
			keys.push(key + ".iad");
		}
		Object rtv = null;
		for (String k : keys) {
			rtv = prop.get(k);
			if (rtv != null) break;
		}
		return rtv == null ? "" : (String) rtv;
	}

	public static void setConf(String key, String value) {
		if (prop == null) _init();
		prop.put(key, value);
	}

	private static void _init() {
		try {
			prop = new Properties();
			FileInputStream stream = new FileInputStream("conf.prop");
			prop.load(stream);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fatal: configuration load failed!");
			System.exit(1);
		}

		Map<String, String> systemEnv = System.getenv();
		String gdEnv = systemEnv.get("GD_ENV");
		if (! StringUtils.isEmpty(gdEnv)) {
			try {
				_env = Env.valueOf(gdEnv.toUpperCase());
				System.out.println("INFO: convert GD_ENV into _env: " + _env);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.out.println("Fatal: No matching environment for GD_ENV: " + gdEnv);
				System.exit(1);
			}
		} else {
			String hostName1 = null;
			String ip = null;
			try {
				hostName1 = InetAddress.getLocalHost().getHostName();
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if (!hostName1.contains("dev") && !ip.matches("^(10\\.20\\.|10\\.0\\.|10\\.32\\.|192\\.168).*")) {
				if (!hostName1.contains("jenkins") && !hostName1.contains("test")) {
					if (hostName1.contains("smoke")) {
						_env = Config.Env.SMOKE;
					} else if (hostName1.contains("stg")) {
						_env = Config.Env.STAGE;
					} else if (!hostName1.startsWith("iad") && !ip.startsWith("10.17.")) {
						_env = Config.Env.PROD;
					} else {
						_env = Config.Env.PROD_IAD;
					}
				} else {
					_env = Config.Env.TEST;
				}
			} else {
				_env = Config.Env.DEV;
			}
		}
	}
}
