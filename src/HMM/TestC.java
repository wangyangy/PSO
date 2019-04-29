package HMM;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TestC {

	public static String date2TimeStamp(String date_str){  
		try {  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +0800",Locale.ENGLISH);
			return String.valueOf(sdf.parse(date_str).getTime()/1000);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return "";  
	}  

	public static String timeStamp2Date(String seconds) {  
		if(seconds == null || seconds.isEmpty() || seconds.equals("null")){  
			return "";  
		}  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		return sdf.format(new Date(Long.valueOf(seconds+"000")));  
	}  
	
	public static void testList(List<String> list) {
		for(int i=0;i<3;i++) {
			list.remove(new Random().nextInt(list.size()));
		}
	}



	public static double nextDouble(final double min, final double max) {
		double d = min + ((max - min) * new Random().nextDouble());;
		double newd = (double)Math.round(d*1000)/1000;
		return newd;
	}
	
	public static List<String> getSubStringByRadom(List<String> list, int count){
		List<String> backList = null;
		backList = new ArrayList<String>();
		Random random = new Random();
		int backSum = 0;
		if (list.size() >= count) {
			backSum = count;
		}else {
			backSum = list.size();
		}
		HashSet<Integer> set = new HashSet<>();
		while(set.size()<backSum) {
			int target = random.nextInt(list.size());
			set.add(target);
		}
		for(Integer i:set) {
			backList.add(list.get(i));
		}
		return backList;
	}
	
	public static void timeFormat() {
		//22/Mar/2019:11:36:26 +0800
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +0800",Locale.ENGLISH);
		
		Date d = new Date();
		String date = sdf.format(d);
//		String date = sdf.format("2018-10-09 08:35:46");
		System.out.println(date);
	}
	
	public static void testMap() {
		HashMap<String,ArrayList<String>> user_date = new HashMap<>();
		ArrayList<String> list = new ArrayList<>();
		user_date.put("wy", list);
		list.add("asd");
		list.add("gfh");
		//先把list放入map中之后在list中存入数据,看一看map中的list会不会变
		for(String s:user_date.keySet()) {
			System.out.println(s+user_date.get(s));
		}
	}


	public static void main(String[] args) throws InterruptedException, ParseException {
		//		Random r = new Random();
		//		for(int i=0;i<100;i++) {
		//			System.out.println(r.nextInt(10));			
		//		}
		
//		long t1 = System.currentTimeMillis();
//		Thread.sleep(1000);
//		long t2 = System.currentTimeMillis();
//		System.out.println(t1);
//		System.out.println(t2);

//		System.out.println(date2TimeStamp("2018-10-24 14:28:28"));
//		System.out.println(new Date().getTime());
//		System.out.println("-----");
//		Long newtime = Long.parseLong("1539136357")+2;
//		System.out.println("newtime:"+newtime);
//
//		System.out.println(timeStamp2Date("1539136357"));
//		
//		System.out.println(String.valueOf(nextDouble(0,1)));
		
//		ArrayList<String> list = new ArrayList<>();
//		list.add("1");
//		list.add("2");
//		list.add("3");
//		list.add("4");
//		list.add("5");
//		list.add("6");
////		testList(list);
//		for(String s:list) {
//			System.out.println(s);
//		}
//		ArrayList<String> list1 = (ArrayList<String>) getSubStringByRadom(list,3);
//		System.out.println(list1);
//		
		String sss = "yyyy-MM-dd +0800";
		System.out.println(sss.split("-")[0]);
		System.out.println(sss.split("-")[1]);
		System.out.println(sss.split("-")[2]);
		
		timeFormat();
		
		System.out.println(new Date().getTime());
		
		System.out.println("asdas?asdas".split("\\?")[0]);
		
		String s = "_=1553225704152 HTTP/1.1";
		String newStr = s.replace("_=1553225704152", "_="+"15ds225704152");
		System.out.println(newStr);
		
		System.out.println("asdadseditor.phpasd".contains("editor.php"));
		
		int id = new Random().nextInt(1000)+187;
		newStr = "GET /personal/editor.php?id=187 HTTP/1.1".replace("id=187", "id="+id);
		System.out.println(newStr);
		
//		System.out.println(date2TimeStamp("2018-10-09 09:43:31"));
		
//		System.out.println(date2TimeStamp("2018-10-09 09:53:31"));
		System.out.println(timeStamp2Date("1539049411"));
		
		
		System.out.println("GET /logi HTTP/1.1".replace("/logi", "//"+"asd"));
		System.out.println("*******");
//		System.out.println(date2TimeStamp("09/Oct/2018:08:35:46 +0800"));
		
		
		String sd = "123.125.71.39 - - [03/Dec/2018:18:34:45 +0800] \"GET /page/articleAPI.php_=1543833285 HTTP/1.1\" 304 26977 \"http://10.245.146.90/personal/docmanagement.php\" \"Opera/9.80 (Windows NT 5.2; U; zh-cn) Presto/2.7.62 Version/11.01\"";
		String []sds = sd.split("\"");
//		System.out.println(sds[sds.length-1]);
		for(String ssss:sds) {
			System.out.println(ssss);
		}
		
		timeFormat();
//		System.out.println(date2TimeStamp("23/Mar/2019:17:21:45 +0800"));
		
		System.out.println("%%%%%%%%%%%%%%%%%");
		System.out.println(date2TimeStamp("2018-10-10 11:20:23"));
		System.out.println(date2TimeStamp("2018-10-10 11:50:23"));
		System.out.println(timeStamp2Date("1539045346"));
		
//		int k=0;
//		for(k=0;k<10;k++) {
//			if(k==3) {
//				if(true) {
//					if(true) {
//						break;
//					}
//				}
//			}
//		}
//		System.out.println(k);
		
//		testMap();
		
		int a = 10;
		Double c = (double) a;
		System.out.println(c);
		
		System.out.println(Double.parseDouble("10"));
	}

}
