package benworks.comet.socket.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import benworks.comet.socket.PacketMap;
import benworks.comet.socket.pkt.Field;
import benworks.comet.socket.pkt.Packet;

/**
 * 用于生成Packet描述文件的类
 * 
 * @author benworks
 * 
 */
public class PacketDoc {

	private static Comparator<Entry<String, Field>> fieldComparator = new Comparator<Entry<String, Field>>() {

		@Override
		public int compare(Entry<String, Field> o1, Entry<String, Field> o2) {
			return o1.getValue().index() - o2.getValue().index();
		}
	};

	private static Comparator<Entry<Short, Map<String, Object>>> packetComparator = new Comparator<Entry<Short, Map<String, Object>>>() {

		@Override
		public int compare(Entry<Short, Map<String, Object>> o1,
				Entry<Short, Map<String, Object>> o2) {
			return o1.getKey() - o2.getKey();
		}
	};

	public static void createPacketPage() {
		Map<Short, Map<String, Object>> packetInfoMap = new HashMap<Short, Map<String, Object>>();
		// packet header
		Map<String, Object> headerInfoMap = new HashMap<String, Object>();
		short headerType = 0;
		headerInfoMap.put("packetType", headerType);
		headerInfoMap.put("packetName", "PacketHeader");
		collectPacketField(Packet.class, headerInfoMap);
		packetInfoMap.put(headerType, headerInfoMap);
		// general packet
		Map<Short, Class<? extends Packet>> allPacketClasses = PacketMap
				.getAllPacketClasses();
		for (Entry<Short, Class<? extends Packet>> entry : allPacketClasses
				.entrySet()) {
			Map<String, Object> infoMap = new HashMap<String, Object>();
			short packetType = entry.getKey();
			Class<? extends Packet> packetClass = entry.getValue();
			infoMap.put("packetType", packetType);
			String packetName = packetClass.getSimpleName();
			infoMap.put("packetName", packetName);
			collectPacketField(packetClass, infoMap);
			packetInfoMap.put(packetType, infoMap);
		}
		List<Entry<Short, Map<String, Object>>> packetList = new ArrayList<Entry<Short, Map<String, Object>>>(
				packetInfoMap.entrySet());
		Collections.sort(packetList, packetComparator);
		createPage(packetList);
	}

	private static void collectPacketField(Class<? extends Packet> packetClass,
			Map<String, Object> infoMap) {
		java.lang.reflect.Field[] fields = packetClass.getDeclaredFields();
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		for (java.lang.reflect.Field field : fields) {
			Field packetField = field.getAnnotation(Field.class);
			if (packetField == null)
				continue;
			if (packetField.index() == 0) {
				String packetDesc = packetField.name();
				infoMap.put("packetDesc", packetDesc);
				infoMap.put("packetCatalog", packetField.catalog());
			} else {
				fieldMap.put(field.getName(), packetField);
				infoMap.put(field.getName() + "-length",
						getLength(field.getType()));
			}
		}
		List<Entry<String, Field>> fieldList = new ArrayList<Entry<String, Field>>(
				fieldMap.entrySet());
		Collections.sort(fieldList, fieldComparator);
		infoMap.put("fieldList", fieldList);
	}

	private static int getLength(Class<?> type) {
		int len = 0;
		if (type == Integer.class || type == int.class)
			len = 4;
		else if (type == Short.class || type == short.class)
			len = 2;
		else if (type == Byte.class || type == byte.class)
			len = 1;
		return len;
	}

	@SuppressWarnings("unchecked")
	private static void createPage(
			List<Entry<Short, Map<String, Object>>> packetList) {
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element rootElement = document.createElement("packets");
			document.appendChild(rootElement);
			for (Entry<Short, Map<String, Object>> entry : packetList) {
				Element packetElement = document.createElement("packet");
				rootElement.appendChild(packetElement);
				Map<String, Object> valueMap = entry.getValue();
				packetElement.setAttribute("type", valueMap.get("packetType")
						.toString());
				packetElement.setAttribute("name", valueMap.get("packetName")
						.toString());
				packetElement.setAttribute("catalog",
						valueMap.get("packetCatalog").toString());
				packetElement.setAttribute("desc", valueMap.get("packetDesc")
						.toString());
				List<Entry<String, Field>> fieldList = (List<Entry<String, Field>>) valueMap
						.get("fieldList");
				StringBuilder packetFormat = new StringBuilder();
				for (int i = 0; i < fieldList.size(); i++) {
					Entry<String, Field> fieldEntry = fieldList.get(i);
					Element fieldElement = document.createElement("field");
					String fieldName = fieldEntry.getKey();
					Field field = fieldEntry.getValue();
					packetFormat.append("[" + fieldName + "]");
					if (i < fieldList.size() - 1)
						packetFormat.append(" + ");
					fieldElement.setAttribute("name", fieldName);
					fieldElement.setAttribute("index",
							String.valueOf(field.index()));
					fieldElement.setAttribute("length",
							valueMap.get(fieldName + "-length").toString());
					fieldElement.setAttribute("desc", field.name());
					packetElement.appendChild(fieldElement);
				}
				packetElement.setAttribute("format", packetFormat.toString());
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory
					.newTransformer(new StreamSource(PacketDoc.class
							.getResourceAsStream("/packet.xsl")));
			// Transformer transformer = transformerFactory.newTransformer();
			Source input = new DOMSource(document);
			File pageFileDir = new File(PacketDoc.class.getResource("/")
					.toURI());
			File pageFile = new File(pageFileDir, "packet.html");
			OutputStream outputStream = new FileOutputStream(pageFile);
			Result output = new StreamResult(outputStream);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(input, output);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PacketDoc.createPacketPage();
	}
}
