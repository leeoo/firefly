package com.firefly.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firefly.template.parser.ViewFileReader;

public class TemplateFactory {

	private Config config;
	private Map<String, View> map = new HashMap<String, View>();

	public TemplateFactory() {

	}

	public TemplateFactory(String path) {
		this.config = new Config();
		config.setViewPath(path);
	}

	public TemplateFactory(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
	public TemplateFactory init() {
		if(config == null)
			throw new IllegalArgumentException("template config is null");
		
		ViewFileReader reader = new ViewFileReader(config);
		List<String> javaFiles = reader.getJavaFiles();
		List<String> templateFiles = reader.getTemplateFiles();
		List<String> classNames = reader.getClassNames();
		
		for (int i = 0; i < javaFiles.size(); i++) {
			String c = javaFiles.get(i);
			final String classFileName = c.substring(0, c.length() - 4) + "class";
			ClassLoader classLoader = new ClassLoader(){
				@Override
				public Class<?> findClass(String name) {
					BufferedInputStream bis = null;
					byte[] b = null;
					try {
						File file = new File(classFileName);
						b = new byte[(int)file.length()];
						bis = new BufferedInputStream(new FileInputStream(file));
						bis.read(b);
					} catch (Throwable e) {
						Config.LOG.error("read class file error", e);
					} finally {
						if(bis != null)
							try {
								bis.close();
							} catch (IOException e) {
								Config.LOG.error("close error", e);
							}
					}
					
					return defineClass(name, b, 0, b.length);
				}
			};
			
			try {
				map.put(templateFiles.get(i), (View)classLoader.loadClass(classNames.get(i)).newInstance());
			} catch (Throwable e) {
				Config.LOG.error("load class error", e);
			}
		}
		return this;
	}
	
	public View getView(String name) {
		return map.get(name);
	}

}
