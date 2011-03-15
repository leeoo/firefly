package com.firefly.core.support.annotation;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanReader;
import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.utils.ReflectUtils;
import com.firefly.utils.VerifyUtils;

/**
 * 读取Bean信息
 * 
 * @author AlvinQiu
 * 
 */
public class AnnotationBeanReader implements BeanReader {
	private static Logger log = LoggerFactory
			.getLogger(AnnotationBeanReader.class);
	protected List<BeanDefinition> beanDefinitions;
	protected Set<String> idSet;

	public AnnotationBeanReader() {
		this(null);
	}

	public AnnotationBeanReader(String file) {
		beanDefinitions = getBeanDefinitions();
		idSet = new HashSet<String>();
		Config config = ConfigReader.getInstance().load(file);
		for (String pack : config.getPaths()) {
			log.info("componentPath [{}]", pack);
			scan(pack.trim());
		}
	}

	protected List<BeanDefinition> getBeanDefinitions() {
		return new ArrayList<BeanDefinition>();
	}

	private void scan(String pack) {
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
					addClassesByFile(packageName, filePath);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else if ("jar".equals(protocol)) {
				log.info("protocol [{}]", protocol);
				try {
					JarFile jar = ((JarURLConnection) url.openConnection())
							.getJarFile();

					addClassesByJar(packageName, packageDirName, jar);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addClassesByJar(String packageName, String packageDirName,
			JarFile jar) throws ClassNotFoundException {
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

					if (isComponent(c)) {
						log.info("classes [{}]", c.getName());
						// TODO 增加bean定义
						addBeanDefinition(c);
					}
				}
			}
		}
	}

	private void addClassesByFile(String packageName, String filePath)
			throws ClassNotFoundException {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
		});

		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				addClassesByFile(packageName + "." + file.getName(),
						file.getAbsolutePath());
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				Class<?> c = AnnotationBeanReader.class.getClassLoader()
						.loadClass(packageName + '.' + className);

				if (isComponent(c)) {
					log.info("classes [{}]", c.getName());
					// TODO 增加bean定义
					addBeanDefinition(c);
				}

			}
		}
	}

	protected boolean isComponent(Class<?> c) {
		return c.isAnnotationPresent(Component.class);
	}

	protected void addBeanDefinition(Class<?> c) {
		AnnotationBeanDefinition annotationBeanDefinition = new AnnotatedBeanDefinition();
		annotationBeanDefinition.setClassName(c.getName());

		Component component = c.getAnnotation(Component.class);
		String id = component.value();
		if (VerifyUtils.isNotEmpty(id)) {
			if (idSet.contains(id))
				error("id: " + id + " duplicate error");
			annotationBeanDefinition.setId(id);
			idSet.add(id);
		}

		Set<String> names = ReflectUtils.getInterfaceNames(c);
		annotationBeanDefinition.setInterfaceNames(names);

		List<Field> fields = getInjectField(c);
		annotationBeanDefinition.setInjectFields(fields);

		List<Method> methods = getInjectMethod(c);
		annotationBeanDefinition.setInjectMethods(methods);

		try {
			Object object = c.newInstance();
			annotationBeanDefinition.setObject(object);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		beanDefinitions.add(annotationBeanDefinition);
	}

	@Override
	public List<BeanDefinition> loadBeanDefinitions() {
		return beanDefinitions;
	}

	protected List<Field> getInjectField(Class<?> c) {
		Field[] fields = c.getDeclaredFields();
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			if (field.getAnnotation(Inject.class) != null) {
				list.add(field);
			}
		}
		return list;
	}

	protected List<Method> getInjectMethod(Class<?> c) {
		Method[] methods = c.getDeclaredMethods();
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.isAnnotationPresent(Inject.class)) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 处理异常
	 * 
	 * @param msg
	 *            异常信息
	 */
	protected void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
