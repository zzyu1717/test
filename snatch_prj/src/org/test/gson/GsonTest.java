package org.test.gson;

 
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
* 
* @author zzy 2017年8月31日
* @version
*/
public class GsonTest {
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
//		Gson json = new GsonBuilder().setPrettyPrinting().create();
		Gson json = new Gson();
		Province[] ps = json.fromJson(new FileReader("D:\\prj_experience\\snatchGit\\snatch_prj\\resources\\province.json"),    Province[].class);
		City[] cs = json.fromJson(new FileReader("D:\\prj_experience\\snatchGit\\snatch_prj\\resources\\city.json"),   City[].class);
		
		for (City c : cs) {
			String parentCode =  c.getProvince();
			String parentName = "";
			for (Province p : ps) {
				if (p.getProvince().equals(parentCode)) {
					parentName = p.getName();
					break;
				}
			}
			System.out.println(c.getCity() + " " + c.getName() + " " + parentCode +  " " + parentName);
		}
	}
}
