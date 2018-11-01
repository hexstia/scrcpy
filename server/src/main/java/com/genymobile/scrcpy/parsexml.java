package com.genymobile.scrcpy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class Type {
	private String t, t1, t2, t3, t4, t5;
	private String root_name, name, t_name, t1_name, t2_name, t3_name, t4_name, t5_name;

	public Type(String root_name, String name, String t_name, String t1_name, String t2_name, String t3_name,
			String t4_name, String t5_name) {
		super();
		this.root_name = root_name;
		this.name = name;
		this.t_name = t_name;
		this.t1_name = t1_name;
		this.t2_name = t2_name;
		this.t3_name = t3_name;
		this.t4_name = t4_name;
		this.t5_name = t5_name;
	}

	public String getRoot_name() {
		return root_name;
	}

	public void setRoot_name(String root_name) {
		this.root_name = root_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getT_name() {
		return t_name;
	}

	public void setT_name(String t_name) {
		this.t_name = t_name;
	}

	public String getT1_name() {
		return t1_name;
	}

	public void setT1_name(String t1_name) {
		this.t1_name = t1_name;
	}

	public String getT2_name() {
		return t2_name;
	}

	public void setT2_name(String t2_name) {
		this.t2_name = t2_name;
	}

	public String getT3_name() {
		return t3_name;
	}

	public void setT3_name(String t3_name) {
		this.t3_name = t3_name;
	}

	public String getT4_name() {
		return t4_name;
	}

	public void setT4_name(String t4_name) {
		this.t4_name = t4_name;
	}

	public String getT5_name() {
		return t5_name;
	}

	public void setT5_name(String t5_name) {
		this.t5_name = t5_name;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getT1() {
		return t1;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public String getT2() {
		return t2;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

	public String getT3() {
		return t3;
	}

	public void setT3(String t3) {
		this.t3 = t3;
	}

	public String getT4() {
		return t4;
	}

	public void setT4(String t4) {
		this.t4 = t4;
	}

	public String getT5() {
		return t5;
	}

	public void setT5(String t5) {
		this.t5 = t5;
	}
}

class SAXParserHandler extends DefaultHandler {
	String value = null;
	Type book = null;
	private ArrayList<Type> bookList = new ArrayList<Type>();
	private String root_name, name, t_name, t1_name, t2_name, t3_name, t4_name, t5_name;

	public ArrayList<Type> getBookList() {
		return bookList;
	}

	SAXParserHandler(String root_name, String name, String t_name, String t1_name, String t2_name, String t3_name,
			String t4_name, String t5_name) {
		this.root_name = root_name;
		this.name = name;
		this.t_name = t_name;
		this.t1_name = t1_name;
		this.t2_name = t2_name;
		this.t3_name = t3_name;
		this.t4_name = t4_name;
		this.t5_name = t5_name;
	}

	int bookIndex = 0;

	/**
	 * 用来标识解析开始
	 */
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	/**
	 * 用来标识解析结束
	 */
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	/**
	 * 解析xml元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// 调用DefaultHandler类的startElement方法
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(name)) {
			bookIndex++;
			// 创建一个book对象
			book = new Type(root_name, name, t_name, t1_name, t2_name, t3_name, t4_name, t5_name);
			// 开始解析book元素的属性
			// 不知道book元素下属性的名称以及个数，如何获取属性名以及属性值
			int num = attributes.getLength();
			for (int i = 0; i < num; i++) {
				if (attributes.getQName(i).equals(t_name)) {
					book.setT(attributes.getValue(i));
				}
			}
		} else if (!qName.equals(t1_name) && !qName.equals(root_name)) {
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// 调用DefaultHandler类的endElement方法
		super.endElement(uri, localName, qName);
		// 判断是否针对一本书已经遍历结束
		if (qName.equals(name)) {
			bookList.add(book);
			book = null;
		} else if (qName.equals(t1_name)) {
			book.setT1(value);
		} else if (qName.equals(t2_name)) {
			book.setT2(value);
		} else if (qName.equals(t3_name)) {
			book.setT3(value);
		} else if (qName.equals(t4_name)) {
			book.setT4(value);
		} else if (qName.equals(t5_name)) {
			book.setT5(value);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		value = new String(ch, start, length);
		if (!value.trim().equals("")) {
//			System.out.println("节点值是：" + value);
		}
	}
}

public class parsexml {
	/**
	 * @param arg[0] xmlfile
	 * @param arg[1] root_name
	 * @param arg[2] name
	 * @param arg[3] id
	 * @param arg[4] ip
	 * @param arg[5] port_heart
	 * @param arg[6] port_Scem_cont
	 * @param arg[7] ..
	 * @param arg[8] ..
	 */
	public static ArrayList<Type> parse(String[] args) {
		// 锟斤拷取一锟斤拷SAXParserFactory锟斤拷实锟斤拷
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// 通锟斤拷factory锟斤拷取SAXParser实锟斤拷
		try {
			SAXParser parser = factory.newSAXParser();
			// 锟斤拷锟斤拷SAXParserHandler锟斤拷锟斤拷
			SAXParserHandler handler = new SAXParserHandler(args[1], args[2], args[3], args[4], args[5], args[6],
					args[7], args[8]);
			parser.parse(args[0], handler);
			System.out.println("~！~！~！共有" + handler.getBookList().size() + "服务器");
			for (Type book : handler.getBookList()) {
				System.out.println(book.getT());
				System.out.println(book.getT1());
				System.out.println(book.getT2());
				System.out.println(book.getT3());
				System.out.println(book.getT4());
				System.out.println(book.getT5());
				System.out.println("----finish----");
			}
			return handler.getBookList();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
