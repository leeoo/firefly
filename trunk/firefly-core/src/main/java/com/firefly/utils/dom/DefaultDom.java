package com.firefly.utils.dom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DefaultDom implements Dom{

	private final String file;
	
	public DefaultDom() {
		this(null);
	}
	
	public DefaultDom(String file) {
		this.file = (file == null ? "firefly.xml" : file);
	}
	
	@Override
	public Document getDocument() {
		
		Document doc = null;
		
		// 得到dom解析器工厂实例
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			// 得到dom解析器
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputStream is = DefaultDom.class.getResourceAsStream("/"+this.file);
			
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return doc;
	}

	@Override
	public Element getRoot(Document doc) {
		return doc.getDocumentElement();
	}

	@Override
	public List<Element> elements(Element e) {
		return elements(e,null);
	}
	
	@Override
	public List<Element> elements(Element e, String name) {
		List<Element> eList = new ArrayList<Element>();
		
		NodeList nodeList = e.getChildNodes();
		for(int i = 0;i < nodeList.getLength();++i){
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				if(name != null){
					if(node.getNodeName().equals(name))
						eList.add((Element)node);
				}else{
					eList.add((Element)node);
				}
			}
		}
		return eList;
	}
	
	@Override
	public Element element(Element e, String name) {
		NodeList element = e.getElementsByTagName(name);
		if(element != null && e.getNodeType() == Node.ELEMENT_NODE){
			return (Element)element.item(0);
		}
		return null;
	}
	
	@Override
	public String getTextValue(Element valueEle) {
		if(valueEle != null){
			StringBuilder sb = new StringBuilder();
			NodeList nl = valueEle.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node item = nl.item(i);
				if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
					sb.append(item.getNodeValue());
				}
			}
			return sb.toString();
		}
		return null;		
	}
}