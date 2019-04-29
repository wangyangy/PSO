package HMM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public class PrepareLogData {


	/**
	 * 格式化时间
	 */
	public static String timeFormat(Date date) {
		//22/Mar/2019:11:36:26 +0800
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +0800",Locale.ENGLISH);
		String d = sdf.format(date);
		return d;
	}

	public static File createFileByDay(Date date) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = sdf.format(date);
		String yyyy_MM_dd = d.split(" ")[0];
		String yyyy = yyyy_MM_dd.split("-")[0];
		String MM = yyyy_MM_dd.split("-")[1];
		String dd = yyyy_MM_dd.split("-")[2];
		String path = "data\\log\\access_"+yyyy+"_"+MM+"_"+dd+".log";
		File file = new File(path);
		//文件不存在就创建新的文件
		if(!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 先获取时间以及时间对应的IP地址
	 * @throws Exception
	 */
	public static HashMap<String,ArrayList<String>> getDate_IP() throws Exception{
		File file = new File("data\\newData\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,ArrayList<String>> user_date_IP = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String IP1 = obj.getString("clientIP");
			String IP2 = obj.getString("IP");
			String userAgnet = obj.getString("浏览器属性信息");
			if(IP1==null && IP2 ==null) {
				continue;
			}
			if(user_date_IP.containsKey(username)) {
				ArrayList<String> list = user_date_IP.get(username);
				String data = date+"&&"+IP1+"&&"+IP2+"&&"+userAgnet;
				list.add(data);
			}else {
				ArrayList<String> list = new ArrayList<>();
				String data = date+"&&"+IP1+"&&"+IP2+"&&"+userAgnet;
				list.add(data);
				user_date_IP.put(username, list);
			}
		}
		br.close();
		return user_date_IP;
	}

	/**
	 * 获取时间戳
	 * @param date_str
	 * @return
	 */
	public static String getTimestamp(String date_str){  
		try {  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
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

	/**
	 * 换一个思路不按照一行一行的顺序创建日志数据,现将用户的所有数据做一个统计
	 * 再按照用户来生成日志数据,在排序
	 * @throws Exception
	 */
	public static void createAccessLog(HashMap<String,ArrayList<String>> user_date_IP) throws Exception{
		File file = new File("data\\newData\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String []loginStrings = new String[] {"GET /personal/indexb.php HTTP/1.1","POST /page/login.php HTTP/1.1",
				"GET /personal/indexb.php HTTP/1.1","GET /js/collect/sendData.js HTTP/1.1",
				"GET /personal/userportal.php HTTP/1.1","POST /personal/dataCollect1.php HTTP/1.1",
				"GET /login.html HTTP/1.1","GET /css/bootstrap.min.css?v=3.3.6 HTTP/1.1",
				"GET /css/font-awesome.css?v=4.4.0 HTTP/1.1","GET /css/style.css?v=4.1.0 HTTP/1.1",
				"GET /css/animate.css HTTP/1.1","GET /js/jquery.min.js?v=2.1.4 HTTP/1.1",
				"GET /js/jquery.md5.js HTTP/1.1","GET /list.php?more=2&page=2 HTTP/1.1"};

		String []dynaminStrings = new String[] {"GET /personal/docmanagement.php HTTP/1.1",
				"GET /js/collect/mouse_keydown.js HTTP/1.1",
				"GET /page/articleAPI.php?_=1553225704152 HTTP/1.1",
				"POST /page/articleinfo.php HTTP/1.1",
				"POST /personal/dataCollect2.php HTTP/1.1",
				"GET /personal/docmanagement.php HTTP/1.1",
				"GET /personal/editor.php?id=187 HTTP/1.1",
				"GET /personal/editor/plugins/table-dialog/table-dialog.js HTTP/1.1",
		"POST /page/upcontent.php HTTP/1.1"};

		String []otherStrings = new String[] {"GET /personal/userlist.php HTTP/1.1",
				"GET /personal/serverinfo.php HTTP/1.1",
				"GET /personal/userinfo.php HTTP/1.1",
				"GET /personal/developlogs.php HTTP/1.1",
				"GET /personal/project.php HTTP/1.1",
				"GET /personal/usergets.php HTTP/1.1",
				"POST /personal/dataCollect2.php HTTP/1.1",
		"GET /list.php?more=2&page=1 HTTP/1.1"};

		String []code = new String[] {"302","200","304"};
		String []code_abnormal = new String[] {"404","403","408","500"};
		String line = null;
		ArrayList<String> result = new ArrayList<>();
		while((line = br.readLine())!=null) {
			try {
				//按照文本的顺序解析json字符串
				JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
				String username = obj.getString("username");
				//2018-10-09 08:35:46
				String date = obj.getString("date");
				String IP1 = obj.getString("clientIP");
				String IP2 = obj.getString("IP");
				ArrayList<String> res1 = null,res2 = null,res3 = null,res4 = null;
				//这是登录的情形
				if(date!=null &&(IP1==null && IP2==null)) {
					res1 = createLoginLog(user_date_IP, loginStrings, code, username, date);
				}
				//这是动态数据的情形,没有IP地址,要从传入的参数中根据最近时间来获取IP
				if(date==null && IP1==null && IP2==null) {
					res2 = createDynamicLog(user_date_IP, dynaminStrings, code, obj, username);
				}
				//这是浏览器数据的情形,这种情形中出现错误和杂项
				if(date!=null&&IP1!=null&&IP2!=null) {
					res3 = createBrowserLog(user_date_IP, otherStrings, code, username, date);
				}
				//这时候要针对这个模拟的IP地址进行错误日志,404,408等等
				if(IP1!=null && ("123.125.71.39".equals(IP1))){ 
					//异常用户也不是一定就要异常的
					if(new Random().nextInt(10)<5) {
						res4 = createErrorLog(user_date_IP, code_abnormal, username, date,"123.125.71.39",false);						
					}
				}
				//对正常账户也要有一些异常操作,不过频率相对要少
				else if(IP1!=null && (!"123.125.71.39".equals(IP1))){ 
					res4 = createErrorLog(user_date_IP, code_abnormal, username, date,IP2,true);
				}

				if(res1!=null) {
					result.addAll(res1);
					//					System.out.print(res1.size()+"    ");
				}
				if(res2!=null) {
					result.addAll(res2);
					//					System.out.print(res2.size()+"    ");
				}
				if(res3!=null) {
					result.addAll(res3);
					//					System.out.print(res3.size()+"    ");
				}
				if(res4!=null) {
					result.addAll(res4);
					//					System.out.print(res4.size()+"    ");
				}
				//				System.out.println("---------------------------");

			}catch (Exception e) {
				System.out.println(e);
				System.out.println("解析json文件出错了");
			}
		}

		File file1 = new File("data\\log\\access.log");
		FileWriter fw = new FileWriter(file1);
		BufferedWriter bw = new BufferedWriter(fw);
		for(int i=0;i<result.size();i++) {
			bw.write(result.get(i));
		}
		System.out.println(result.size());
		bw.flush();
		bw.close();
		br.close();
	}

	

	public static void paixu() throws Exception{
		File file = new File("data\\log\\access.log");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		File file1 = new File("data\\log\\access_paixu.log");
		FileWriter fw = new FileWriter(file1);
		BufferedWriter bw = new BufferedWriter(fw);
		//这会把键一样的数据覆盖掉的,要手动的给键变一变
		TreeMap<String, String> map = new TreeMap<>();
		String s = null;
		int i=0;
		while((s=br.readLine())!=null && s.length()>0) {
			i+=1;
			try {
				//这是解析日志时间的
				String ss = s.split("\\[")[1];
				String date = ss.split("\\]")[0];
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +0800",Locale.ENGLISH);	
				Date d = sdf.parse(date);
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = sdf1.format(d);
				date = date+(i+"");
				map.put(date, s);
				
			} catch (Exception e) {
				System.out.println(i);
			}

		}
		for(String key:map.keySet()) {
			bw.write(map.get(key)+"\n");
		}
		bw.flush();
		br.close();
		bw.close();
	}

	public static void splitFile() throws Exception{
		File file = new File("data\\log\\access_paixu.log");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String s = null;
		while((s=br.readLine())!=null && s.length()>0) {
			String ss = s.split("\\[")[1];
			String date = ss.split("\\]")[0];
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +0800",Locale.ENGLISH);
			String timeSamp = String.valueOf(sdf.parse(date).getTime()/1000);  
			String d = timeStamp2Date(timeSamp);
			SimpleDateFormat sdf_ =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date_ = sdf_.parse(d);
			File file_ = createFileByDay(date_);
			FileWriter fw = new FileWriter(file_, true);
			fw.write(s+"\n");
			fw.close();
		}
		br.close();
	}

	private static ArrayList<String> createErrorLog(HashMap<String, ArrayList<String>> user_date_IP, String[] code_abnormal,
			String username, String date,String IP,boolean isNormal) throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		//		String IP = "123.125.71.39";
		String timeStamp = getTimestamp(date);
		String []ips_userAgent = getIP(user_date_IP, username, timeStamp);
		int num = 0;
		Random r = new Random();
		//异常用户的error数目
		if(isNormal==false) {
			//不是正常的用户但是还有正常的登录行为的
			num = r.nextInt(5)+2;			
		}else {
			num = r.nextInt(3);
			if(new Random().nextInt(10)<7) {
				num = 0;
			}
		}
		for(int i=0;i<num;i++) {
			String res = "";
			res+=IP;
			res+=" - - [";
			Random rr = new Random();
			int timeS = rr.nextInt(100);
			String newTimeS = String.valueOf(Long.parseLong(timeStamp)+timeS);
			String dd = timeStamp2Date(newTimeS);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = sdf.parse(dd);
			String formatDate = timeFormat(d);
			res+=formatDate;
			res+="] ";
			if(new Random().nextInt(10)<3) {
				res+="\"-\" 408 - \"-\" \"-\"";
				res+="\n";
				list.add(res);
			}else {
				String errorString  = "GET /logi HTTP/1.1";
				String yangben = "abcdefghigklmnopqrstuvwxyz";
				int l = new Random().nextInt(9)+3;
				StringBuffer sb = new StringBuffer();
				for(int j=0;j<l;j++) {
					sb.append(yangben.charAt(new Random().nextInt(yangben.length())));
				}

				String newEerrorString = errorString.replace("/logi","/"+sb.toString());
				res+=newEerrorString;
				res+="\" ";
				res+=code_abnormal[num%code_abnormal.length];
				res+=" ";
				res+="\"-\" \"-\" ";
				res+=ips_userAgent[2];
				res+="\"";
				res+="\n";
				list.add(res);
			}
		}
		return list;
	}

	private static ArrayList<String> createBrowserLog(HashMap<String, ArrayList<String>> user_date_IP, String[] otherStrings,
			String[] code, String username, String date) throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		String timeStamp = getTimestamp(date);
		String []ips_userAgent = getIP(user_date_IP, username, timeStamp);
		if(ips_userAgent.length<3) {
			System.out.println("有IP地址没获取到");
			throw new RuntimeException();
		}
		Random r = new Random();
		int num = r.nextInt(5);
		int byteNum = new Random().nextInt(65500)+100;
		String []refers = new String[] {"http://10.245.146.90/personal/indexb.php",
				"http://10.245.146.90/personal/userlist.php",
		"http://10.245.146.90/"};
		//产生登录的数据
		ArrayList<Integer> nums = new ArrayList<>();
		for(int k=0;k<num;k++) {
			String res = "";
			if("123.125.71.39".equals(ips_userAgent[0])) {
				res+=ips_userAgent[0];
			}else {
				res+=ips_userAgent[1];
			}
			res+=" - - [";
			Random rr = new Random();
			int timeS = rr.nextInt(1200);
			String newTimeS = String.valueOf(Long.parseLong(timeStamp)+timeS);
			String dd = timeStamp2Date(newTimeS);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = sdf.parse(dd);
			String formatDate = timeFormat(d);
			res+=formatDate;
			res+="] \"";
			int index = -1;
			while(true) {
				index = new Random().nextInt(otherStrings.length);
				if(nums.contains(index)) {
					continue;
				}else {
					break;
				}
			}
			nums.add(index);
			String otherString = otherStrings[index];
			//GET /list.php?more=2&page=1 HTTP/1.1
			String refer = "";
			if(otherString.contains("page")) {
				String newStr = otherString.replace("page=1", "page="+(new Random().nextInt(10)+1));
				otherString = newStr;
				refer = refers[2];
			}
			if(otherString.contains("dataC")) {
				refer = refers[1];
			}
			refer = refers[0];
			res+=otherString;
			res+="\" ";
			res+=code[num%code.length];
			res+=" ";
			res+=(byteNum+"");
			res+=" \"";
			if(new Random().nextInt(6)<1) {
				res+="-\"";
			}else {
				res+=refer+"\"";							
			}
			res+=" \"";
			res+=ips_userAgent[2];
			res+="\"";
			res+="\n";
			list.add(res);
		}
		return list;
	}

	private static ArrayList<String> createDynamicLog(HashMap<String, ArrayList<String>> user_date_IP, String[] dynaminStrings,
			String[] code, JSONObject obj, String username) throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		String timeIn = obj.getString("timeIn");
		String timeStamp = getTimestamp(timeIn);
		String []ips_userAgent = getIP(user_date_IP, username, timeStamp);
		if(ips_userAgent.length<3) {
			System.out.println("有IP地址没获取到");
			throw new RuntimeException();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = sdf.parse(timeIn);
		String formatDate = timeFormat(d);
		Random r = new Random();
		int num = r.nextInt(3);
		num += 1;
		int byteNum = new Random().nextInt(65500)+100;
		String []refers = new String[] {"http://10.245.146.90/personal/docmanagement.php",
				"http://10.245.146.90/personal/editor.php",
		"http://10.245.146.90/personal/indexb.php"};
		//产生登录的数据
		ArrayList<Integer> nums = new ArrayList<>();
		for(int k=0;k<num;k++) {
			String res = "";
			if("123.125.71.39".equals(ips_userAgent[0])) {
				res+=ips_userAgent[0];
			}else {
				res+=ips_userAgent[1];
			}
			res+=" - - [";
			res+=formatDate;
			res+="] \"";
			int index = -1;
			while(true) {
				index = new Random().nextInt(dynaminStrings.length);
				if(nums.contains(index)) {
					continue;
				}else {
					break;
				}
			}
			String dynamicString = dynaminStrings[index];
			String newString = "";
			//"GET /page/articleAPI.php?_=1553225704152 HTTP/1.1",
			//"GET /personal/editor.php?id=187 HTTP/1.1",
			if(dynamicString.contains("articleAPI")) {
				String s = dynamicString.split("\\?")[1];
				String newStr = s.replace("_=1553225704152", "_="+timeStamp);
				newString = dynamicString.split("\\?")[0]+newStr;
				dynamicString  = newString;
			}
			if(dynamicString.contains("editor.php")) {
				int id = new Random().nextInt(1000)+187;
				newString = dynamicString.replace("id=187", "id="+id);
				dynamicString  = newString;
			}
			nums.add(index);
			res+=dynamicString;
			res+="\" ";
			res+=code[num%code.length];
			res+=" ";
			res+=(byteNum+"");
			res+=" \"";
			if(new Random().nextInt(6)<1) {
				res+="-\"";
			}else {
				res+=refers[new Random().nextInt(6)%refers.length]+"\"";							
			}
			res+=" \"";
			res+=ips_userAgent[2];
			res+="\"";
			res+="\n";
			list.add(res);
		}
		return list;
	}

	private static ArrayList<String> createLoginLog(HashMap<String, ArrayList<String>> user_date_IP, String[] loginStrings,
			String[] code, String username, String date) throws ParseException {
		ArrayList<String> list = new ArrayList<>();
		String timeStamp = getTimestamp(date);
		String []ips_userAgent = getIP(user_date_IP, username, timeStamp);
		if(ips_userAgent.length<3) {
			System.out.println("有IP地址没获取到");
			throw new RuntimeException();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = sdf.parse(date);
		String formatDate = timeFormat(d);
		Random r = new Random();
		int num = r.nextInt(8);
		num += 5;
		int byteNum = new Random().nextInt(65500)+100;
		String refer = "http://10.245.146.90/login.html";
		//产生登录的数据
		ArrayList<Integer> nums = new ArrayList<>();
		for(int k=0;k<num;k++) {
			String res = "";
			if("123.125.71.39".equals(ips_userAgent[0])) {
				res+=ips_userAgent[0];
			}else {
				res+=ips_userAgent[1];
			}
			res+=" - - [";
			res+=formatDate;
			res+="] \"";
			int index = -1;
			while(true) {
				index = new Random().nextInt(loginStrings.length);
				if(nums.contains(index)) {
					continue;
				}else {
					break;
				}
			}
			String loginString = loginStrings[index];
			nums.add(index);
			res+=loginString;
			res+="\" ";
			res+=code[num%code.length];
			res+=" ";
			res+=(byteNum+"");
			res+=" \"";
			if(new Random().nextInt(6)<1) {
				res+="-\"";
			}else {
				res+=refer+"\"";							
			}
			res+=" \"";
			res+=ips_userAgent[2];
			res+="\"";
			res+="\n";
			list.add(res);
		}
		return list;
	}

	private static String[] getIP(HashMap<String, ArrayList<String>> user_date_IP, String username, String timeStamp) {
		ArrayList<String> list = user_date_IP.get(username);
		Long del = Long.MAX_VALUE;
		String res_ip1 = null;
		String res_ip2 = null;
		String userAgent = null;
		//list中的元素是这样的: 2018-10-11 11:25:17&&221.2.164.23&&10.241.74.238&&Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14
		for(int i=0;i<list.size();i++) {
			String data = list.get(i);
			String [] datas = data.split("&&");
			String time = datas[0];
			String ip1 = datas[1];
			String ip2 = datas[2];
			String ua = datas[3];
			String timeStamp1 = getTimestamp(time);
			Long time1 = Long.parseLong(timeStamp);
			Long time2 = Long.parseLong(timeStamp1);
			if(del>Math.abs(time1-time2)) {
				del = Math.abs(time1-time2);
				res_ip1 = ip1;
				res_ip2 = ip2;
				userAgent = ua;
			}
		}
		return new String[] {res_ip1,res_ip2,userAgent};
	}

	public static void main(String[] args) throws Exception {
		//		
//		HashMap<String,ArrayList<String>> map = getDate_IP();
//		createAccessLog(map);
		//		
		//		
		//				for(String username:map.keySet()) {
		//					System.out.println(username);
		//					ArrayList<String> datas = map.get(username);
		//					for(int i=0;i<datas.size();i++) {
		//						System.out.println("    "+datas.get(i));				
		//					}
		//				}

//				paixu();

		splitFile();
	}


}
