package com.github.felixvolo.ts5ai.model;

import java.io.IOException;
import java.io.StringReader;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.github.felixvolo.ts5ai.util.StringOutputStream;

public class Packer {
	private static final String NODE_SRCRIPT = "script";
	private static final String NODE_STYLE = "style";
	private static final QName SRC_ATTRIBUTE = new QName("src");
	
	public static String pack(Addon addon, String injectionString, IAddonSource source, String addonRoot) throws Exception {
		if(addon.getSources().isEmpty()) {
			return injectionString;
		}
		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader("<addon>" + injectionString + "</addon>"));
		Element root = document.getRootElement();
		packElement(root, source, addonRoot, addon.getSources());
		XMLWriter writer = new XMLWriter();
		writer.setEscapeText(false);
		StringOutputStream output = new StringOutputStream();
		writer.setOutputStream(output);
		for(Element element : root.elements()) {
			writer.write(element);
		}
		return output.toString();
	}
	
	private static void packElement(Element element, IAddonSource source, String addonRoot, String sources) throws IOException {
		if(NODE_SRCRIPT.equals(element.getName()) || NODE_STYLE.equals(element.getName())) {
			Attribute src = element.attribute(SRC_ATTRIBUTE);
			if(src != null && src.getValue().startsWith(sources)) {
				String data = source.read(addonRoot + src.getValue());
				element.remove(src);
				element.setText(data);
			}
		}
		// We want <script></script> and not <script/>
		if(NODE_SRCRIPT.equals(element.getName()) && !element.hasContent()) {
			element.setText("");
		}
		for(Element child : element.elements()) {
			packElement(child, source, addonRoot, sources);
		}
	}
}
