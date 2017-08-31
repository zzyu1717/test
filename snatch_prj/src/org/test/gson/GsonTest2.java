package org.test.gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.stream.JsonWriter;

/**
* 
* @author zzy 2017年8月31日
* @version
*/
public class GsonTest2 {
	public static void main(String[] args) {
		String html = "<table class='countytable'>\n" + 
		"<tr class='countyhead'>\n" + 
		"<td width=150>统计用区划代码</td><td>名称</td></tr>\n" + 
		"<tr class='countytr'><td><a href='01/110101.html'>110101000000</a></td><td><a href='01/110101.html'>东城区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110102.html'>110102000000</a></td><td><a href='01/110102.html'>西城区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110105.html'>110105000000</a></td><td><a href='01/110105.html'>朝阳区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110106.html'>110106000000</a></td><td><a href='01/110106.html'>丰台区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110107.html'>110107000000</a></td><td><a href='01/110107.html'>石景山区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110108.html'>110108000000</a></td><td><a href='01/110108.html'>海淀区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110109.html'>110109000000</a></td><td><a href='01/110109.html'>门头沟区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110111.html'>110111000000</a></td><td><a href='01/110111.html'>房山区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110112.html'>110112000000</a></td><td><a href='01/110112.html'>通州区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110113.html'>110113000000</a></td><td><a href='01/110113.html'>顺义区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110114.html'>110114000000</a></td><td><a href='01/110114.html'>昌平区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110115.html'>110115000000</a></td><td><a href='01/110115.html'>大兴区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110116.html'>110116000000</a></td><td><a href='01/110116.html'>怀柔区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110117.html'>110117000000</a></td><td><a href='01/110117.html'>平谷区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110118.html'>110118000000</a></td><td><a href='01/110118.html'>密云区</a></td></tr>"
		+ "<tr class='countytr'><td><a href='01/110119.html'>110119000000</a></td><td><a href='01/110119.html'>延庆区</a></td></tr>\n" + 
		"</table>";
		
		parseHtml(html);
		
		
	}
	
	public static void parseHtml(String html) {
		Document doc = Jsoup.parse(html);
		
		Elements elements = doc.select("tr.countytr");
		List<City> citys = new ArrayList<>();
		for (Element e : elements) {
			String text = e.text();
			String[] data = text.split(" ");
			String code = data[0];
			String name = data[1];
			City city = new City();
			city.setCity(code);
			city.setName(name);
			city.setType("CITY");
			citys.add(city);
		}
		
		printJson(citys);
	}
	
	public static void writeJsonStream(OutputStream out, List<City> message)   {
		JsonWriter writer;
		try {
			writer = new JsonWriter(new OutputStreamWriter(out,"utf-8"));
			writer.setIndent("  ");
			writeMessageArray(writer, message);
			
			writer.close();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeMessageArray(JsonWriter writer, List<City> message) {
		try {
			writer.beginArray();
			for (City c : message) {
				writeMessageObject(writer, c);
			}
			writer.endArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void writeMessageObject(JsonWriter writer, City city) {
		try {
			writer.beginObject();
			
			writer.name("city").value(city.getName());
			writer.name("code").value(city.getCity());
			writer.name("type").value(city.getType());
			
			writer.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printJson(List<City> message) {
//		PrintStream out = System.out;
		FileOutputStream out;
		try {
			out = new FileOutputStream("C:\\Users\\ZZY\\Desktop\\city.json");
			writeJsonStream(out, message);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

























































