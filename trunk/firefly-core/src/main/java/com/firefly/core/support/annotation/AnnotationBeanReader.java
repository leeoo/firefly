package com.firefly.core.support.annotation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Component;
import com.firefly.annotation.Controller;
import com.firefly.annotation.Interceptor;
import com.firefly.core.support.BeanReader;

/**
 * 读取Bean信息
 *
 * @author AlvinQiu
 *
 */
public class AnnotationBeanReader implements BeanReader {
	private static Logger log = LoggerFactory
			.getLogger(AnnotationBeanReader.class);
	private Set<Class<?>> classes;
	private Properties properties;
	public static final String COMPONENT_PATH = "componentPath";

	private AnnotationBeanReader() {

	}

	private static class AnnotationBeanReaderHolder {
		private static AnnotationBeanReader instance = new AnnotationBeanReader();
	}

	public static AnnotationBeanReader getInstance() {
		return AnnotationBeanReaderHolder.instance;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	public BeanReader load() {
		return load(null);
	}

	public BeanReader load(String file) {
		properties = new Properties();
		try {
			properties.load(AnnotationBeanReader.class.getResourceAsStream("/"
					+ (file != null ? file : DEFAULT_CONFIG_FILE)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String[] componentPath = properties.getProperty(COMPONENT_PATH)
				.split(",");

		for (String pack : componentPath) {
			log.info("componentPath [{}]", pack);
			scan(pack.trim());
		}
		return this;
	}

	private void scan(String pack) {
		classes = new LinkedHashSet<Class<?>>();
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs = null;

		try {

			dirs = AnnotationBeanReader.class.getClassLoader().getResources(
					packageDirName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 循环迭代下去
		while (dirs.hasMoreElements()) {
			// 获取下一个元素
			URL url = dirs.nextElement();
			// 得到协议的名称
			String protocol = url.getProtocol();
			// 如果是以文件的形式保存在服务器上
			if ("file".equals(protocol)) {
				log.info("protocol [{}]", protocol);
				// 获取包的物理路径
				String filePath = null;
				try {
					filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// 以文件的方式扫描整个包下的文件 并添加到集合中
				try {
					addClassesByFile(packageName, filePath, classes);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else if ("jar".equals(protocol)) {
				log.info("protocol [{}]", protocol);
				try {
					JarFile jar = ((JarURLConnection) url.openConnection())
							.getJarFile();

					addClassesByJar(packageName, packageDirName, jar, classes);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addClassesByJar(String packageName, String packageDirName,
			JarFile jar, Set<Class<?>> classes) throws ClassNotFoundException {
		// 从此jar包 得到一个枚举类
		Enumeration<JarEntry> entries = jar.entries();
		// 同样的进行循环迭代
		while (entries.hasMoreElements()) {
			// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			// 如果是以/开头的
			if (name.charAt(0) == '/') {
				// 获取后面的字符串
				name = name.substring(1);
			}
			// 如果前半部分和定义的包名相同
			if (name.startsWith(packageDirName)) {
				int idx = name.lastIndexOf('/');
				// 如果以"/"结尾 是一个包
				if (idx != -1) {
					// 获取包名 把"/"替换成"."
					packageName = name.substring(0, idx).replace('/', '.');
				}
				// 如果可以迭代下去 并且是一个包
				if (idx != -1 && name.endsWith(".class")
						&& !entry.isDirectory()) {
					// 如果是一个.class文件 而且不是目录
					// 去掉后面的".class" 获取真正的类名
					String className = name.substring(packageName.length() + 1,
							name.length() - 6);
					// 添加到classes
					Class<?> c = AnnotationBeanReader.class.getClassLoader()
							.loadClass(packageName + '.' + className);

					if (isAnnotationPresent(c)) {
						log.info("classes [{}]", c.getName());
						classes.add(c);
					}
				}
			}
		}
	}

	private void addClassesByFile(String packageName, String filePath,
			Set<Class<?>> classes) throws ClassNotFoundException {
		// 获取此包的目录 建立一个File
		File dir = new File(filePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				addClassesByFile(packageName + "." + file.getName(), file
						.getAbsolutePath(), classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				// 添加到集合中去
				// classes.add(Class.forName(packageName + '.' +
				// className));
				// 这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
				Class<?> c = AnnotationBeanReader.class.getClassLoader()
						.loadClass(packageName + '.' + className);

				if (isAnnotationPresent(c)) {
					log.info("classes [{}]", c.getName());
					classes.add(c);
				}

			}
		}
	}

	private boolean isAnnotationPresent(Class<?> c) {
		return c.isAnnotationPresent(Controller.class)
				|| c.isAnnotationPresent(Component.class)
				|| c.isAnnotationPresent(Interceptor.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

}
