package com.firefly.core.support.annotation;

import java.io.IOException;
import java.util.Properties;

import com.firefly.utils.StringUtils;

public class ConfigReader {
	private static final String DEFAULT_CONFIG_FILE = "firefly.properties";
	private Config config;

	private ConfigReader() {

	}

	private static class Holder {
		private static ConfigReader instance = new ConfigReader();
	}

	public static ConfigReader getInstance() {
		return Holder.instance;
	}

	public Config load(String file) {
		Properties properties = new Properties();
		config = new Config();
		try {
			properties.load(ConfigReader.class.getResourceAsStream("/"
					+ (file != null ? file : DEFAULT_CONFIG_FILE)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		config.setPaths(StringUtils.split(properties
				.getProperty("componentPath"), ","));
		config.setViewPath(properties.getProperty("viewPath"));
		config.setEncoding(properties.getProperty("encoding"));
		return config;
	}

	public Config getConfig() {
		return config;
	}
}
