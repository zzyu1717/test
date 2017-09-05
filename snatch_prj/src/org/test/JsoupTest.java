package org.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author zzy
 * @version
 */
public class JsoupTest {
	private static final String SITE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/index.html";

	public static void main(String[] args) throws Exception {
		getProvince();
	}

	/**
	 * 获取省份
	 */
	private static void getProvince() {
		Document doc = open(SITE_URL);
		Elements links = doc.select("tr.provincetr").select("a");
		long startTimeStamp = System.currentTimeMillis();
		for (int i = 0; i < links.size(); i++) {
			Element e = links.get(i);
			RegionEntry province = new RegionEntry();
			String href = e.attr("href"); // 14.html
			String[] arr = href.split("\\.");
			String name = e.text();
			StringBuffer code = new StringBuffer(arr[0]);
			while (code.length() < 12) {
				code.append("0");
			}

			province.setCode(code.toString());
			province.setName(name);
//			System.out.println(code + " == " + e.text());
			String absHref = e.attr("abs:href");

			// 创建一个新的线程
			Thread t = new Thread(name) {
				/**
				 *               
				 */
				@Override
				public void run() {
					try {
						File file = createFile(name);
						PrintWriter writer = new PrintWriter(file);
						printSQL(name, code.toString(), null, writer);
						getCity(absHref, province, writer);
						writer.close();
						long endTimeStamp = System.currentTimeMillis();
						System.out.println("********************" + name + " finish done! 程序共执行了：" + (endTimeStamp-startTimeStamp)*1.0/(1000*60) + "分钟");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}

				}
			};
			t.start();
		}
	}

	private static File createFile(String name) {
		File file = new File("C:\\Users\\ZZY\\Desktop\\2016\\" + name + ".sql");
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return file;
	}

	/**
	 * 获取市
	 * 
	 * @param url
	 * @param province
	 * @param writer
	 */
	private static void getCity(String url, RegionEntry province, PrintWriter writer) {
		Document doc = open(url);
		Elements links = doc.select("tr.citytr");
		RegionEntry city;
		for (Element e : links) {
			city = new RegionEntry();
			Elements alist = e.select("a");
			Element codeE = alist.get(0);
			Element codeN = alist.get(1);
			String name = codeN.text();

			String code = codeE.text();

			/**
			 *  
			 */
			printSQL(name, code, province.getCode(), writer);
			// System.out.println(code + "==" + name);
			city.setCode(code);
			city.setName(name);

			String absHref = codeE.attr("abs:href");
			getCounty(absHref, city, writer);
		}

	}

	/**
	 * 获取县
	 * 
	 * @param url
	 * @param city
	 * @param writer
	 */
	private static void getCounty(String url, RegionEntry city, PrintWriter writer) {
		Document doc = open(url);
		Elements links = doc.select("tr.countytr");
		RegionEntry county;
		for (Element e : links) {
			county = new RegionEntry();
			Elements alist = e.select("a");
			if (alist.size() > 0) {
				Element codeE = alist.get(0);

				String code = codeE.text();
				county.setCode(code);

				Element codeN = alist.get(1);
				String name = codeN.text();
				county.setName(name);

				String absHref = codeE.attr("abs:href");
				getTown(absHref, county, writer);

				/**
				 *  
				 */
				printSQL(name, code, city.getCode(), writer);
				// System.out.println(code + "==" + name);
			} else {
				alist = e.select("td");
				String name = alist.get(1).text();
				String code = alist.get(0).text();
				county.setCode(code);
				county.setName(name);
				printSQL(name, code, city.getCode(), writer);
//				System.out.println(alist.get(0).text() + "=***=" + alist.get(1).text());
			}

		}

	}

	/**
	 * 获取城镇
	 * 
	 * @param url
	 * @param county
	 */
	private static void getTown(String url, RegionEntry county, PrintWriter writer) {
		Document doc = open(url);
		Elements links = doc.select("tr.towntr");
		RegionEntry town;
		for (Element e : links) {
			town = new RegionEntry();
			Elements alist = e.select("a");
			Element codeE = alist.get(0);
			Element codeN = alist.get(1);
			String name = codeN.text();

			String code = codeE.text();

			/**
			 *  
			 */
			printSQL(name, code, county.getCode(), writer);
			// System.out.println(code + "==" + name);
			town.setCode(code);
			town.setName(name);

			String absHref = codeE.attr("abs:href");
			getVilage(absHref, town, writer);
		}

	}

	/**
	 * 获取乡村
	 * 
	 * @param absHref
	 * @param county
	 */
	private static void getVilage(String url, RegionEntry town, PrintWriter writer) {
		Document doc = open(url);
		Elements links = doc.select("tr.villagetr");
		RegionEntry village;
		for (Element e : links) {
			village = new RegionEntry();
			Elements alist = e.select("td");
			Element codeE = alist.get(0);
			Element codeN = alist.get(2);
			String name = codeN.text();
			String code = codeE.text();

			/**
			 *  
			 */
			printSQL(name, code, town.getCode(), writer);
			// System.out.println(code + "==" + name);
			village.setCode(code);
			village.setName(name);

		}

	}

	private static Document open(String url) {
		Document doc = null;
		try {
			doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
		} catch (Exception e) {
			try {

				doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);

			} catch (Exception e1) {
				try {
					doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return doc;
	}

	/**
	 * 输出SQL语句
	 * 
	 * @param name
	 * @param code
	 * @param parentCode
	 * @param writer
	 */
	private static void printSQL(String name, String code, String parentCode, PrintWriter writer) {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into test0014 (name, code, parent_code) Values('").append(name).append("','").append(code)
				.append("','").append(parentCode).append("');");
		writer.println(sb.toString());
	}

	private static PrintWriter getWriter() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("C:\\Users\\ZZY\\Desktop\\data.sql");
			return writer;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
