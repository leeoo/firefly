package com.firefly.core.support.xml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanDefinitionReader;

/**
 * 读取XML文件
 * @author 杰然不同
 * @date 2010-11-29
 * @Version 1.0 */
public class XmlBeanDefinitionReader implements BeanDefinitionReader {

	private final String fileName;
	
	protected static Logger log = Logger.getLogger(XmlBeanDefinitionReader.class.getName());
	
	public XmlBeanDefinitionReader(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * 读取配置文件,将Bean信息存入HashMap中
	 * @Date 2010-12-5
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BeanDefinition> loadBeanDefinitions(){
		
		Map<String, BeanDefinition> beanDefinitionsMap = new HashMap<String, BeanDefinition>();
		
		Document doc = null;
		
		// 获得Xml文档对象
		try {
			log.info("Get XML Document");
			doc = readDocument(this.fileName);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		
		// 获得根节点
		List<Element> beans = doc.getRootElement().elements("bean");
		
		// 遍历所有跟节点
		for(Element e : beans){
			
			BeanDefinition beanDefinitionan = new BeanDefinition();
			String id = e.attributeValue("id");
			String className = e.attributeValue("class");
			beanDefinitionan.setId(id);
			beanDefinitionan.setClassName(className);
			
			// 获得Bean中所有property
			List<Element> propertiesList = e.elements("property");
			
			// 遍历所有property
			for(Element e1 : propertiesList){
				String name = e1.attributeValue("name");
		
				// 如果是普通赋值形式
				if(e1.attribute("value") != null)
					beanDefinitionan.getProperties().put(name, e1.attributeValue("value"));
				
				// 如果是引用另一个Bean形式
				if(e1.attribute("ref") != null)
					beanDefinitionan.getProperties().put(name, e1.attributeValue("ref"));
			}
			
			beanDefinitionsMap.put(id, beanDefinitionan);
		}
		return beanDefinitionsMap;
	}
	
	/**
	 * 根据文件读取Document
	 * @Date 2010-11-28
	 * @param filePath
	 * @return 文档对象
	 * @throws DocumentException 
	 */
	private Document readDocument(String filePath) throws DocumentException{
		// 获得带上classpath路径的文件路径
		filePath = Thread.currentThread().getContextClassLoader().getResource(
				filePath).getPath().substring(1);

		log.info("Loading XML bean definitions from file [" + filePath + "]");
		
		//使用SAXReader来读取xml文件
		SAXReader reader = new SAXReader();
		Document doc = null;
		doc = reader.read(new File(filePath));
		return doc;
	}
}
