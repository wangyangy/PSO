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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public class PrepareData {

	/*
	 * 存储用户对应的插件的
	 */
	static HashMap<String, ArrayList<String>> user_subplugins = new HashMap<>();
	
	/**
	 * 存储用户的分辨率
	 */
	static HashMap<String, String> user_wh = new HashMap<>();
	
	/*
	 * 存储用户IP
	 */
	static HashMap<String, ArrayList<String>> user_IP = new HashMap<>();
	
	/*
	 * 存储已经使用的IP
	 */
	static HashSet<String> usedIP = new HashSet<>();
	/**
	 * 存储IP地址
	 */
	static ArrayList<String> IPS = new ArrayList<>();
	
	/**
	 * 存储公网IP
	 */
	static 	HashMap<String, ArrayList<String>> clientIP = new HashMap<>();
	
	
	/** 
	 * 获取随机日期 
	 * @param beginDate 起始日期，格式为：yyyy-MM-dd 
	 * @param endDate 结束日期，格式为：yyyy-MM-dd 
	 * @return 
	 */  
	private static String randomDate(String beginDate,String endDate){  
		try {  
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
			Date start = format.parse(beginDate);  // 构造开始日期  
			Date end = format.parse(endDate);  // 构造结束日期  
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if(start.getTime() >= end.getTime()){  
				return null;  
			}  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while(true){
				long date = random(start.getTime(),end.getTime());  
				Date d = new Date(date);
				String randomDate = sdf.format(d);
				String HHmmss = randomDate.split(" ")[1];
				String HH = HHmmss.split(":")[0];
				if(HH.contains("00")||HH.contains("22")||HH.contains("01")
						||HH.contains("02")||HH.contains("03")||HH.contains("04")||
						HH.contains("05")||HH.contains("06")||HH.contains("07")||HH.contains("23")) {
					continue;
				}else {
					return sdf.format(d);  
				}
			}

			//			return sdf.format(d);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return null;  
	}  


	private static long random(long begin,long end){  
		long rtn = begin + (long)(Math.random() * (end - begin));  
		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值  
		if(rtn == begin || rtn == end){  
			return random(begin,end);  
		}  
		return rtn;  
	}

	/*
	 * 替换里面的时间和时间戳,还需要手动的在notepad中编辑一些
	 */
	public static void parse_logindata() throws IOException, ParseException {
		File file = new File("data\\Logindata.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		File file1 = new File("data\\Logindata_.txt");
		FileWriter fw = new FileWriter(file1);
		BufferedWriter bw = new BufferedWriter(fw);
		String s = null;
		while((s=br.readLine())!=null) {
			//			JSONObject obj = (JSONObject) JSONObject.parse(s);
			JSONObject obj = (JSONObject) JSONObject.parseObject(s, Feature.OrderedField);
			String session = obj.getString("session");
			String []sessionData = session.split(",");
			String newDate = randomDate("2018-10-9","2019-01-14"); 
			String shijianchuo = getTimestamp(newDate);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<sessionData.length;i++) {
				if(i==1) {
					sb.append("\"id\":"+"\""+shijianchuo+"\",");
					continue;
				}
				if(i==sessionData.length-1) {
					sb.append(sessionData[i]);
					continue;
				}
				sb.append(sessionData[i]+",");
			}
			obj.put("date", newDate);
//			System.out.println(sb.toString());
			JSONObject newSession = (JSONObject) JSONObject.parseObject(sb.toString(), Feature.OrderedField);;
			obj.put("session",newSession.toJSONString());
			bw.write(obj.toJSONString()+"\n");
		}
		bw.flush();
		br.close();
		bw.close();
	}

	/*
	 * 将时间转换为时间戳
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

	/**
	 * 将时间戳转换为时间
	 * @param seconds
	 * @return
	 */
	public static String timeStamp2Date(String seconds) {  
		if(seconds == null || seconds.isEmpty() || seconds.equals("null")){  
			return "";  
		}  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		return sdf.format(new Date(Long.valueOf(seconds+"000")));  
	}  



	public static void paixu() throws Exception{
		File file = new File("data\\data_normal.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		File file1 = new File("data\\data_normal_paixu.txt");
		FileWriter fw = new FileWriter(file1);
		BufferedWriter bw = new BufferedWriter(fw);
		//这是有序的map,会把键相同的数据覆盖掉的,需要手动的给键变一变
		TreeMap<String, String> map = new TreeMap<>();
		String s = null;
		int i=0;
		while((s=br.readLine())!=null && s.length()>0) {
			i+=1;
			try {
				JSONObject obj = (JSONObject) JSONObject.parseObject(s, Feature.OrderedField);
				String date = obj.getString("date");
				if(date == null) {
					String timeIn =  obj.getString("timeIn");
					timeIn = timeIn+(i+"");
					map.put(timeIn, s);
					continue;
				}
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
	
	/**
	 * 输出一下排序的顺序,检查一下
	 */
	public static void paicu_L() throws Exception{
		File file = new File("data\\Logindata_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String s = null;
		while((s=br.readLine())!=null) {
			JSONObject obj = (JSONObject) JSONObject.parseObject(s, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			System.out.println(username+":"+date);
		}
		br.close();
	}


	public static void createDynamic() throws Exception{
		File file = new File("data\\newData\\Logindata_normal.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		File file1 = new File("data\\newData\\dynamic_normal.txt");
		FileWriter fw = new FileWriter(file1);
		BufferedWriter bw = new BufferedWriter(fw);
		String line = null;
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String message = obj.getString("message");
			if(message!=null&&message.contains("登陆失败")) {
				System.out.println(obj.toJSONString());
				continue;
			}
			String username = obj.getString("username");
			String session = obj.getString("session");
			String date = obj.getString("date");
			String timeStamp = getTimestamp(date);
			Random r = new Random();
			int linesnum = r.nextInt(7);
			if(linesnum==0) {
				int jilv = r.nextInt(3);
				if(jilv==0) {
					//什么也不做,就是0行
				}else{
					linesnum = r.nextInt(7);
				}
			}
			boolean flag = false;
			String lastTimeStampIn = timeStamp; 
			for(int i=0;i<linesnum;i++) {
				JSONObject newobj = new JSONObject(true);
				newobj.put("username", username);
				//对session还要进行特殊的处理,把id改一改
				JSONObject newSession = JSONObject.parseObject(session, Feature.OrderedField);
				Long timeStampIn = Long.parseLong(lastTimeStampIn)+2;
				newSession.put("id", timeStampIn);
				Long newtime = Long.parseLong(timeStamp)+2;
				newSession.put("id", newtime);
				newobj.put("session", newSession);
				//用来判断手否要添加一次多的数据
				String [] datas = null;
				Random r1= new Random();
				if(r1.nextInt(10)<3 && flag == false) {
					flag = true;
					datas = getC_W_K_TimeIn_TimeOut(String.valueOf(timeStampIn),true);
				}else {
					datas = getC_W_K_TimeIn_TimeOut(String.valueOf(timeStampIn),false);
				}
				newobj.put("click", datas[0]);
				newobj.put("wheel", datas[1]);
				newobj.put("key", datas[2]);
				newobj.put("timeIn", datas[3]);
				newobj.put("timeOut", datas[4]);
				newobj.put("time", datas[5]);
				//{"username":"zhangruiqi@wis-eye.com","session":{"id":1552824649,"realname":"zhangruiqi","state":2,"username":"zhangruiqi@wis-eye.com","userlevel":2,"articleid":"181"},"click":"11","wheel":"4","key":"967","timeIn":"2019-03-17 20:18:32","timeOut":"2019-03-17 20:24:09","time":"336.893"}
				String timeStp = getTimestamp(datas[4]);
				lastTimeStampIn = String.valueOf(Long.parseLong(timeStp)+new Random().nextInt(10));
				//				System.out.println(newobj);
				//				Thread.sleep(200);
				bw.write(newobj.toJSONString()+"\n");
			}
			bw.flush();
		}
		br.close();
		bw.close();
	}


	/**
	 * 获取6个数据
	 * @param timeStamp1
	 * @param isWeight
	 * @return
	 */
	public static String[] getC_W_K_TimeIn_TimeOut(String timeStamp1,Boolean isWeight) {
		String timeIn = null;
		String Time = null;
		String timeOut = null;
		String click = null;
		String wheel = null;
		String key = null;
		if(isWeight) {
			timeIn = timeStamp2Date(timeStamp1);
			Random timeR = new Random();
			int time = timeR.nextInt(850);
			time = time+150;
			String weishu = String.valueOf(nextDouble(0,1));
			weishu = weishu.split("\\.")[1];
			Time = String.valueOf(time)+"."+weishu;
			String timeStamp2 = String.valueOf(Long.parseLong(timeStamp1)+time);
			timeOut = timeStamp2Date(timeStamp2);
			Random clickR = new Random();
			int click_ = clickR.nextInt(25);
			Random wheelR = new Random();
			int wheel_ = wheelR.nextInt(100);
			Random keyR = new Random();
			int key_ = keyR.nextInt(2000);
			key_ = key_+200;
			click = String.valueOf(click_);
			wheel = String.valueOf(wheel_);
			key = String.valueOf(key_);

		}else {
			timeIn = timeStamp2Date(timeStamp1);
			Random timeR = new Random();
			int time = timeR.nextInt(100);
			String weishu = String.valueOf(nextDouble(0,1));
			weishu = weishu.split("\\.")[1];
			Time = String.valueOf(time)+"."+weishu;
			String timeStamp2 = String.valueOf(Long.parseLong(timeStamp1)+time);
			timeOut = timeStamp2Date(timeStamp2);
			Random clickR = new Random();
			int click_ = clickR.nextInt(8);
			Random wheelR = new Random();
			int wheel_ = wheelR.nextInt(45);
			Random keyR = new Random();
			int key_ = keyR.nextInt(150);
			click = String.valueOf(click_);
			wheel = String.valueOf(wheel_);
			key = String.valueOf(key_);
		}
		return new String[] {click,wheel,key,timeIn,timeOut,Time};
	}



	/**
	 * 产生min-max之间的浮点数,保留三位小数
	 * @param min
	 * @param max
	 * @return
	 */
	public static double nextDouble(final double min, final double max) {
		double d = min + ((max - min) * new Random().nextDouble());;
		double newd = (double)Math.round(d*1000)/1000;
		return newd;
	}

	public static void getUsersFromLogin() throws Exception {
		File file = new File("data\\Logindata_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		HashSet<String> users = new HashSet<>();
		String line = null;
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			users.add(username);
		}
		for(String user:users) {
			System.out.println(user);
		}
		br.close();
	}
	
	public static HashMap<String, String> getUserPassword() throws Exception {
		File file = new File("data\\source\\users_password.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		HashMap<String, String> user_password = new HashMap<>();
		String line = null;
		while((line = br.readLine())!=null) {
			String []datas = line.trim().split("    ");
			String user = datas[0];
			String password = datas[1];
			user_password.put(user, password);
		}
		br.close();
		return user_password;
	}

	public static HashSet<String> getUser() throws Exception {
		File file = new File("data\\source\\users.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		HashSet<String> users = new HashSet<>();
		String line = null;
		while((line = br.readLine())!=null) {
			String username = line.trim();
			users.add(username);
		}
		br.close();
		return users;
	}

	/*
	 * 每种类型的浏览器对应的userAgent
	 */
	public static HashMap<String, ArrayList<String>> getStaticBrowserInfo() throws Exception {
		File file = new File("data\\source\\browser.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		HashMap<String, ArrayList<String>> map = new HashMap<>();
		String line = null;
		while((line = br.readLine())!=null&&line.length()>0) {			
			String browser = line.split(":")[0];
			String info = line.split(":")[1];
			ArrayList<String> list = null;
			if(browser.contains("IE")) {
				list = map.get("IE");
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(info);
				map.put("IE", list);
			}else if(browser.contains("Firefox")) {
				list = map.get("Firefox");
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(info);
				map.put("Firefox", list);
			}
			else if(browser.contains("Chrome")) {
				list = map.get("Chrome");
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(info);
				map.put("Chrome", list);
			}
			else if(browser.contains("Safari")) {
				list = map.get("Safari");
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(info);
				map.put("Safari", list);
			}
			else if(browser.contains("Opera")) {
				list = map.get("Opera");
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(info);
				map.put("Opera", list);
			}
		}
		br.close();
		return map;
	}
	
	public static ArrayList<String> getStaticPlugins() throws Exception{
		File file = new File("data\\source\\plugins_.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		ArrayList<String> plugins = new ArrayList<>();
		while((line = br.readLine())!=null&&line.length()>0) {		
			plugins.add(line);
		}
		fr.close();
		return plugins;
	}
	
	/**
	 * 随机选取list中的一定数量的元素,不会修改传入的list的
	 * @param list
	 * @param count
	 * @return
	 */
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

	/*
	 * 获取用户一定数量的插件
	 */
	public static ArrayList<String> getNumberPlugins(int num,String user) throws Exception {
		ArrayList<String> plugins = getStaticPlugins();
		//第一次产生插件
		if(!user_subplugins.containsKey(user)) {
			ArrayList<String> subPlugins = (ArrayList<String>) getSubStringByRadom(plugins,new Random().nextInt(num)+4);
			user_subplugins.put(user, subPlugins);
		}else {
			//判断是否有一定的激活改变插件
			if(new Random().nextInt(10)<4) {
				ArrayList<String> subPlugins = user_subplugins.get(user);
				if(subPlugins.size()>15) {					
					subPlugins.remove(subPlugins.size()-1);
				}else if(subPlugins.size()<15) {
					while(true) {
						String p = plugins.get(new Random().nextInt(plugins.size()));
						if(!subPlugins.contains(p)) {
							subPlugins.add(p);
							break;
						}else {
							continue;
						}
					}
				}
			}
		}
		return user_subplugins.get(user);
	}

	/**
	 * 将用户和浏览器对应
	 */
	public static HashMap<String, ArrayList<String>> user_browser() throws Exception {
		HashMap<String, ArrayList<String>> browsers = getStaticBrowserInfo();
		ArrayList<String> useragents = new ArrayList<>();
		for(String broswer:browsers.keySet()) {
			useragents.addAll(browsers.get(broswer));
		}
		HashMap<String, ArrayList<String>> user_browser = new HashMap<>();
		HashSet<String> users = getUser();
		Iterator<String> it = users.iterator();
		while(it.hasNext()) {
			String user = it.next();
			Random r = new Random();
			int num = r.nextInt(2);
			num = num+1;
			ArrayList<String> agents = (ArrayList<String>) getSubStringByRadom(useragents, num);
			user_browser.put(user, agents);
		}
		return user_browser;
	}
	
	public static String getWH(String user) {
		String []whs = new String[] {"1600*1200" ,"1680*1050"  ,"1920*1200" ,"1920*1080","1024*768",
				"1280*800" ,"1280*1024" ,"1280*854" ,"1366*768", "1440*900","1440*1050"};
		if(!user_wh.containsKey(user)) {
			int r = new Random().nextInt(11);
			String wh = whs[r];
			user_wh.put(user, wh);
		}
		return user_wh.get(user);
	}
	
	public static ArrayList<String> getIP() throws Exception{
		File file = new File("data\\source\\IP.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine())!=null&&line.length()>0) {
			IPS.add(line);
		}
		br.close();
		return IPS;
	}
	
	/**
	 * 获取内网IP
	 * @param user
	 * @return
	 * @throws Exception
	 */
	
	public static String getUser_IP(String user) throws Exception {
		//获取IP列表
		if(IPS.size()==0) {
			getIP();
		}
		
		//第一次产生IP
		if(user_IP.containsKey(user)) {
			ArrayList<String> ip = user_IP.get(user);
			return ip.get(new Random().nextInt(ip.size()));
		}else {
			boolean flag = true;
			ArrayList<String> ips = null;
			while(true) {
				ips = (ArrayList<String>) getSubStringByRadom(IPS,3);
				for(int i=0;i<ips.size();i++) {
					if(usedIP.contains(ips.get(i))) {
						flag = false;
						ips.remove(i);
					}
				}
				//获取IP成功
				if(flag==true) {
					break;
				}
				//删除已使用的IP,还有剩余IP,也算获取IP成功
				if(flag==false&&ips.size()>0) {
					break;
				}
			}
			user_IP.put(user, ips);
			usedIP.addAll(ips);
		}
		ArrayList<String> ip = user_IP.get(user);
		return ip.get(new Random().nextInt(ip.size()));
	}
	
	public static String getClientIP(String user) {
		String []clientips = new String[] {"221.2.164.40","221.2.164.21", "221.2.164.25", "221.2.164.44",
				"221.2.164.22","221.2.164.23", "221.2.164.224", "221.2.164.26"	};
		if(clientIP.containsKey(user)) {
			ArrayList<String> clientip = clientIP.get(user);
			return clientip.get(new Random().nextInt(clientip.size()));
		}else {
			int num = new Random().nextInt(2);
			num = num+1;
			HashSet<String> clientip = new HashSet<>();
			for(int i=0;i<num;i++) {
				clientip.add(clientips[new Random().nextInt(clientips.length)]);
			}
			ArrayList<String> cips = new ArrayList<>(clientip);
			clientIP.put(user, cips);
		}
		ArrayList<String> res = clientIP.get(user); 
		return res.get(new Random().nextInt(res.size()));
	}

	/**
	 * 根据登录情况生成用户的浏览器,IP等信息
	 * @throws Exception
	 */
	public static void createBrowserData() throws Exception{
		File file = new File("data\\newData\\Logindata_normal.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		File file1 = new File("data\\newData\\Browserdata_normal.txt");
		FileWriter fw = new FileWriter(file1);
		BufferedWriter bw = new BufferedWriter(fw);
		String line = null;
		HashMap<String, ArrayList<String>> user_browser = user_browser();
		while((line = br.readLine())!=null&&line.length()>0) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String message = obj.getString("message");
			if(message!=null&&message.contains("失败")) {
				System.out.println(obj.toJSONString());
				continue;
			}
			String username = obj.getString("username");
			String date = obj.getString("date");
			String session = obj.getString("session");
			String tjRefer = "http://10.245.146.90/login.html";
			ArrayList<String> agents = user_browser.get(username);
			String agent = agents.get(new Random().nextInt(agents.size()));
			String browserType = null;
			if(agent!=null&&agent.contains("MSIE")) {
				browserType = "IE";
			}else if(agent!=null&&agent.contains("Firefox")) {
				browserType = "Firefox";
			}else if(agent!=null&&agent.contains("Chrome")) {
				browserType = "Chrome";
			}else if(agent!=null&&agent.contains("Safari")) {
				browserType = "Safari";
			}else if(agent!=null&&agent.contains("Opera")) {
				browserType = "Opera";
			}
			String cookie = "true";
			String cpu = "undefined";
//			String MIME = "63";
			int plugin_num = new Random().nextInt(10)+3;
			ArrayList<String> plugins = getNumberPlugins(plugin_num,username);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<plugins.size();i++) {
				sb.append(plugins.get(i)+";");
			}
			String Plugins = sb.toString();
			String language = "undefined";
			String RateW = null;
			String RateH = null;
			String WH = getWH(username);
			RateW = WH.split("\\*")[0];
			RateH = WH.split("\\*")[1];
			String clientIP = getClientIP(username);
			String clientId = "371000";
			String clientName = "山东省威海市";
			String url = "http://10.245.146.90/personal/indexb.php";
			String title = "NIT 最懂你的知识共享平台-WIKI";
			String domain = "10.246.146.90";
			String IP = getUser_IP(username);
	        
			JSONObject newObj = new JSONObject(true);
			newObj.put("username", username);
			newObj.put("date", date);
			newObj.put("session", session);
			newObj.put("tjRefer", tjRefer);
			newObj.put("浏览器类型", browserType);
			newObj.put("浏览器属性信息", agent);
			newObj.put("浏览器的是否启用了cookie", cookie);
			newObj.put("cpu等级", cpu);
			newObj.put("插件的数量", plugin_num);
			newObj.put("插件的名称", Plugins);
			newObj.put("语言", language);
			newObj.put("屏幕分辨率高度", RateH);
			newObj.put("屏幕分辨率宽度", RateW);
			newObj.put("clientIP", clientIP);
			newObj.put("clientId", clientId);
			newObj.put("clientName", clientName);
			newObj.put("url", url);
			newObj.put("title", title);
			newObj.put("domain", domain);
			newObj.put("IP", IP);
			
			bw.write(newObj.toJSONString());
			bw.newLine();
			
		}
		
		bw.flush();
		bw.close();
		br.close();
		
	}
	
	public static void check() {
		
	}
	
	/**
	 * 创建登录数据,最开始的一步,生成数据后要取出里面的一些斜数据
	 */
	public static void createLoginData() throws Exception{
		File file = new File("data\\newData\\Logindata_normal.txt");
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		HashMap<String, String> user_password = getUserPassword();
		for(String user : user_password.keySet()) {
			Random r = new Random();
			int number = r.nextInt(13);
			number = number+12;
			for(int i=0;i<number;i++) {				
				String password = user_password.get(user);
				String newDate = randomDate("2019-02-23","2019-04-15"); 
				String shijianchuo = getTimestamp(newDate);
				//{"id":"1539772397000","username":"mengchen@wis-eye.com","userlevel":"2"}
				String session = "{\"id\":\""+shijianchuo+"\",\"username\":\""+user+"\",\"userlevel\":\"2\"}";
				String message = "登录成功";
				JSONObject obj = new JSONObject(true);
				obj.put("username", user);
				obj.put("password", password);
				obj.put("date", newDate);
				obj.put("session", session);
				obj.put("message", message);
				bw.write(obj.toJSONString());
				bw.newLine();
			}
		}
		bw.close();
		
	}




	public static void main(String[] args) throws Exception {

		
//		createLoginData();
		
//		createBrowserData();
		
		createDynamic();
//		getUsersFromLogin();
//		getStaticBrowserInfo();
				
//				paixu();
		//		createDynamic();
		//		System.out.println(randomDate("2018-10-10","2019-01-14"));
		
//		for(int i=0;i<5;i++) {
//			HashSet<String> users = getUser();
//			Iterator<String> it = users.iterator();
//			while(it.hasNext()) {
//				String user = it.next();
//				System.out.println(user +"::"+getNumberPlugins(10,user));						
//			}
//		}
		
//		user_browser();
		
//		HashSet<String> users = getUser();
//		Iterator<String> it = users.iterator();
//		while(it.hasNext()) {
//			String user = it.next();
////			getUser_IP(user);
//			getClientIP(user);
////			System.out.println(user+"::"+getUser_IP(user));	
////			System.out.println(getUser_IP(user));	
//		}
//		
//		for(String user:clientIP.keySet()) {
//			System.out.println(user+"::"+clientIP.get(user));			
//		}
		
//		paixu();
		
//		paicu_L();
		
//		createBrowserData();
	}


}
