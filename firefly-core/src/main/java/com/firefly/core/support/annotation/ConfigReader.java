package com.firefly.core.support.annotation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.firefly.utils.dom.DefaultDom;
import com.firefly.utils.dom.Dom;

public class ConfigReader {
	private static Logger log = LoggerFactory.getLogger(ConfigReader.class);
	
	private static final String DEFAULT_CONFIG_FILE = "firefly.xml";
	public static final String SCAN_ELEMENT = "component-scan";
	public static final String MVC_ELEMENT = "mvc";
	public static final String PACKAGE_ATTRIBUTE = "base-package";
	public static final String VIEW_PATH_ATTRIBUTE = "view-path";
	public static final String VIEW_ENCODING_ATTRIBUTE = "view-encoding";

	private Config config;

	private ConfigReader() {
		config = new Config();
	}

	private static class Holder {
		private static ConfigReader instance = new ConfigReader();
	}

	public static ConfigReader getInstance() {
		return Holder.instance;
	}

	public Config load(String file) {
		Dom dom = new DefaultDom();
		// 获得Xml文档对象
		Document doc = dom.getDocument(file == null ? DEFAULT_CONFIG_FILE
				: file);
		// 得到根节点
		Element root = dom.getRoot(doc);
		load(root, dom);
		return config;
	}

	public Config load(Element root, Dom dom) {
		// 得到所有scan节点
		List<Element> scanList = dom.elements(root, SCAN_ELEMENT);

		if (scanList != null) {
			String[] paths = new String[scanList.size()];
			for (int i = 0; i < scanList.size(); i++) {
				Element ele = scanList.get(i);
				paths[i] = ele.getAttribute(PACKAGE_ATTRIBUTE);
			}
			config.setPaths(paths);
		} else {
			config.setPaths(new String[0]);
		}

		Element mvc = dom.element(root, MVC_ELEMENT);
		if (mvc != null) {
			String viewPath = mvc.getAttribute(VIEW_PATH_ATTRIBUTE);
			String encoding = mvc.getAttribute(VIEW_ENCODING_ATTRIBUTE);
			log.debug("mvc viewPath [{}] encoding [{}]", viewPath, encoding);
			
			config.setViewPath(viewPath);
			config.setEncoding(encoding);
		}
		return config;
	}

	public Config getConfig() {
		return config;
	}
}
