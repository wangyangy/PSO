package HMM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;


public class StatisticData1 {

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

	public static String date2TimeStamp_(String date_str){  
		try {  
			//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss",Locale.ENGLISH);
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
	 * 这是将所有的混合数据,统计出登录,浏览器,动态数据,这里还没有进行相应的会话结合,在huihua函数中才进行了相应的会话结合
	 * 还要考虑怎么把这一步在hadoop中实现,思路是分别实现最后在组合成一次会话数据
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,ArrayList<String>> parseCollecedData() throws Exception{
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,ArrayList<String>> user_date = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String IP1 = obj.getString("clientIP");
			String IP2 = obj.getString("IP");
			String browserType = obj.getString("浏览器类型");
			String userAgnet = obj.getString("浏览器属性信息");
			String message = obj.getString("message");
			String time = obj.getString("time");
			String click = obj.getString("click");
			String wheel = obj.getString("wheel");
			String key = obj.getString("key");
			String timeIn = obj.getString("timeIn");
			//这是登录的情形,存储相应的登录数据
			if(IP1==null && IP2 ==null && message!=null) {
				if(user_date.containsKey(username)) {
					ArrayList<String> list = user_date.get(username);
					String data = date+"&&"+message;
					list.add(data);
					//这里不用在把list put到map中了,因为map中存放的就是引用,引用的值变了自然也会跟着变的
				}else {
					ArrayList<String> list = new ArrayList<>();
					String data = date+"&&"+message;
					list.add(data);
					user_date.put(username, list);
				}
			}
			//这是userAgnet以及Ip地址的信息
			if(IP1!=null && IP2 !=null) {
				if(user_date.containsKey(username)) {
					ArrayList<String> list = user_date.get(username);
					String data = date+"&&"+IP1+"&&"+IP2+"&&"+browserType+"&&"+userAgnet;
					list.add(data);
				}else {
					ArrayList<String> list = new ArrayList<>();
					String data = date+"&&"+IP1+"&&"+IP2+"&&"+browserType+"&&"+userAgnet;
					list.add(data);
					user_date.put(username, list);
				}
			}
			//dynamic信息
			if(date==null) {
				if(user_date.containsKey(username)) {
					ArrayList<String> list = user_date.get(username);
					String data = timeIn+"&&"+click+"&&"+wheel+"&&"+key+"&&"+time;
					list.add(data);
				}else {
					ArrayList<String> list = new ArrayList<>();
					String data = timeIn+"&&"+click+"&&"+wheel+"&&"+key+"&&"+time;
					list.add(data);
					user_date.put(username, list);
				}
			}
		}
		br.close();
		return user_date;
	}


	/**
	 * 这里只解析了出错的日志
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> parseLogDate() throws Exception{
		File file = new File("data\\access_paixu.log");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		ArrayList<String> error = new ArrayList<>();
		while((line = br.readLine())!=null) {
			String []datas = line.split(" ");
			String ip = datas[0];
			//[13/Jan/2019:20:40:38
			String date = datas[3];
			date = date.replace("[", "");
			date = date.replace("]", "");
			String []d = line.split("\"");
			String code = "";
			//不能简写为一个if条件判断,因为code不好直接解析出来,所有要手动判断,在手动填写
			if(line.contains(" 408 ")) {
				code = "408";
				String timeStamp = date2TimeStamp_(date);
				String formatDate = timeStamp2Date(timeStamp);
				String userAgeent = d[d.length-1];
				String res = ip+"&&"+formatDate+"&&"+code+"&&"+userAgeent;
				error.add(res);
			}
			if(line.contains(" 403 ")) {
				code = "403";
				String timeStamp = date2TimeStamp_(date);
				String formatDate = timeStamp2Date(timeStamp);
				String userAgeent = d[d.length-1];
				String res = ip+"&&"+formatDate+"&&"+code+"&&"+userAgeent;
				error.add(res);
			}
			if(line.contains(" 404 ")) {
				code = "404";
				String timeStamp = date2TimeStamp_(date);
				String formatDate = timeStamp2Date(timeStamp);
				String userAgeent = d[d.length-1];
				String res = ip+"&&"+formatDate+"&&"+code+"&&"+userAgeent;
				error.add(res);
			}
			if(line.contains(" 500 ")) {
				code = "500";
				String timeStamp = date2TimeStamp_(date);
				String formatDate = timeStamp2Date(timeStamp);
				String userAgeent = d[d.length-1];
				String res = ip+"&&"+formatDate+"&&"+code+"&&"+userAgeent;
				error.add(res);
			}
		}
		br.close();
		return error;
	}

	/**
	 * 按照会话统计数据,这是很重要的
	 * @throws Exception
	 */
	public static TreeMap<String, ArrayList<String>> huihua(ArrayList<String> collectDatas) throws Exception{
		String line = null;
		//键是用户+时间,只要是时间差不超过半个小时的就算一次会话
		TreeMap<String, ArrayList<String>> user_session_data = new TreeMap<>();
		int len = collectDatas.size();
		for(int i=0;i<len;i++) {
			line = collectDatas.get(i);
			String []datas = line.split("&&");
			String username = datas[0];
			String date = datas[1];
			String timeStamp = date2TimeStamp(date);
			//没有这个键
			if(!user_session_data.containsKey(username+"_"+timeStamp)) {
				boolean flag = false;
				for(String user_time:user_session_data.keySet()) {
					String u = user_time.split("_")[0];
					String t = user_time.split("_")[1];
					//姓名相同了,在比较时间
					if(u.equals(username)) {
						//这两个时间戳的单位是秒,小于1800说明是在半个小时之内的
						if(Math.abs(Long.parseLong(t)-Long.parseLong(timeStamp))<1800) {
							flag = true;
							ArrayList<String> data = user_session_data.get(user_time);
							if(data==null) {
								data = new ArrayList<>();
								for(int k=2;k<datas.length;k++) {
									data.add(datas[k]);								 
								}
								user_session_data.put(user_time, data);
							}else {
								for(int k=2;k<datas.length;k++) {
									data.add(datas[k]);								 
								}
								user_session_data.put(user_time, data);
							}
						}
					}
				}
				if(!flag) {
					ArrayList<String> list = new ArrayList<>();
					for(int k=2;k<datas.length;k++) {
						list.add(datas[k]);								 
					}
					user_session_data.put(username+"_"+timeStamp, list);
				}
				//含有这个键,追加数据	
			}else {
				ArrayList<String> data = user_session_data.get(username+"_"+timeStamp);
				if(data!=null) {
					for(int k=2;k<datas.length;k++) {
						data.add(datas[k]);								 
					}
					user_session_data.put(username+"_"+timeStamp, data);
				}
			}
		}
		return user_session_data;
	}


	public static ArrayList<String> user_log(ArrayList<String> logs) throws Exception {
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		ArrayList<String> data = new ArrayList<>();
		while((line = br.readLine())!=null) {
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String clientIp = obj.getString("clientIP");
			String IP = obj.getString("IP");
			if(clientIp!=null&&IP!=null) {
				data.add(username+"&&"+date);
			}
		}
		br.close();
		ArrayList<String> log_user = new ArrayList<>();
		line = null;
		int len = logs.size();
		for(int q=0;q<len;q++) {
			line = logs.get(q);
			String []datas = line.split("&&");
			//			String ip = datas[0];
			String date = datas[1];
			String tiemStamp = date2TimeStamp(date);
			String username = "";
			for(int i=0;i<data.size();i++) {
				String user = data.get(i).split("&&")[0];
				String time = data.get(i).split("&&")[1];
				String timeS = date2TimeStamp(time);
				//这个100是根据生成数据的时候设置的
				if(Math.abs(Long.parseLong(tiemStamp)-Long.parseLong(timeS))<100) {
					username = user;
					break;
				}
			}
			if(username!=null&&username.length()>0) {
				String newline = username+"&&"+line;
				log_user.add(newline);
			}
		}
		return log_user;
	}


	/**
	 * 判断这次会话是正常访问还是风险访问,条件分别是:1.识别客户端使用习惯;2.账号短期内频繁登录失败;3.同一个Ip地址登录多个账号(内网);4.后台日志经常报错404
	 * 返回结果就是这次会话是正常/风险/未知
	 */
	public static HashMap<String, String> judgeSession(ArrayList<String> log_user,
			ArrayList<String> result_session,String dateStart,String dateEnd) throws Exception{

		HashMap<String, String> session_status = new HashMap<>();
		HashMap<String,ArrayList<String>> IP_session = getALLIP(dateStart,dateEnd);
		TreeMap<String, ArrayList<String>> user_session_log = getLogErrorBySession(log_user);
		HashMap<String,HashMap<String,Integer>> user_browser = getBrowser(dateStart,dateEnd);
		int len = result_session.size();
		String line = null;
		for(int q=0;q<len;q++) {
			line = result_session.get(q);
			String []datas = line.split("--&&--");
			String username_timeStamp = datas[0];
			String data = datas[1];
			String username = username_timeStamp.split("_")[0];
			String ts = username_timeStamp.split("_")[1];
			//标识,用于判断session是否已经被识别
			boolean flag = false;
			//先判断日志中是否有多次404,如果有就判定为风险登录
			for(String ut:user_session_log.keySet()) {
				String username1 = ut.split("_")[0];
				String ts1 = ut.split("_")[1];
				//用户相同再去比较时间戳
				if(username.equals(username1)) {
					//这个100秒是根据生成数据的时候设置的
					if(Math.abs(Long.parseLong(ts)-Long.parseLong(ts1))<100) {
						//并且要判断异常日志中出现的次数大于等于2,才判定为风险
						ArrayList<String> errorLog = user_session_log.get(ut);
						int k=0;
						for(int i=0;i<errorLog.size();i++) {
							//不能是equals,只能是包含,数据中可能存在空格
							//"404","403","408","500"
							if(errorLog.get(i).contains("404")||errorLog.get(i).contains("403")||
									errorLog.get(i).contains("408")||errorLog.get(i).contains("500")) {
								k++;
							}
						}
						if(k>=2) {
							session_status.put(username_timeStamp, "风险访问");
							flag=true;
							break;
						}
					}
				}
			}
			//判断是否频繁登录失败,识别session状态
			if(!flag) {
				//判断data中是否包含多个登录失败,至少是2个才可以
				data = data.replace("[", "");
				data = data.replace("]", "");
				String []ds = data.split(",");
				int k=0;
				for(String d:ds) {
					if(d.contains("登录失败")) {
						k++;
					}
				}
				if(k>=2) {
					session_status.put(username_timeStamp, "风险访问");
					flag=true;
				}
			}
			//判断同一个IP地址登录多个账号的情形,先统计出所有的IP地址,在判断同一个IP地址登录多个账号(不考虑时间差了)
			if(!flag) {
				//解析data获取IP地址
				data = data.replace("[", "");
				data = data.replace("]", "");
				String []ds = data.split(",");
				String ip = "";
				for(int i=0;i<ds.length;i++) {
					if(ds[i].split("\\.").length>3 && ds[i].startsWith("10")) {
						ip = ds[i];
						break;
					}
				}
				//这可能是猪油一次登录失败的情形
				if(ip!="") {
					ArrayList<String> sessions = IP_session.get(ip);
					//检测这个IP地址是否有多个账号登录
					for(int i=0;i<sessions.size();i++) {
						String user = sessions.get(i).split("_")[0];
						//同一个Ip登录了多个用户
						if(!username.equals(user)) {
							session_status.put(username_timeStamp, "风险访问");
							flag = true;
							break;
						}
					}
				}
			}

			//根据客户端使用习惯来识别是否为正常
			if(!flag) {
				//统计IP浏览器使用次数超过3次变可以认为是资管使用的客户端
				data = data.replace("[", "");
				data = data.replace("]", "");
				String []ds = data.split(",");
				for(String s:ds) {
					//说明s是浏览器类型
					if(onlyContainAlphabet(s.trim())) {
						HashMap<String,Integer> browser_num = user_browser.get(username);
						for(String bro:browser_num.keySet()) {
							if(bro.equals(s.trim())) {
								//使用该浏览访问的次数大于5说明是正常访问
								if(browser_num.get(bro)>5) {
									session_status.put(username_timeStamp, "正常访问");
									flag = true;
									break;
								}
							}
						}
					}
				}
			}
			//上面的情形都不是,则定义为未知访问,也可以改为风险,
			if(!flag) {
				session_status.put(username_timeStamp, "未知访问");
			}
		}
		return session_status;
	}

	public static boolean onlyContainAlphabet(String str) {
		for(int i=0;i<str.length();i++) {
			char c = str.charAt(i);
			if(!Character.isLetter(c)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 获取用户浏览器使用习惯,为了判断是否为正常访问的
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,HashMap<String,Integer>> getBrowser(String dateStart,String dateEnd) throws Exception{
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,HashMap<String,Integer>> user_browser = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String IP = obj.getString("IP");
			//说明数据中没有要的浏览器类型直接跳过
			if(IP==null) {
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = sdf.parse(date);
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			if(dt.getTime()>end.getTime()||dt.getTime()<start.getTime()) {
				continue;
			}
			String browserType = obj.getString("浏览器类型");
			if(user_browser.containsKey(username)) {
				HashMap<String,Integer> browser_num = user_browser.get(username);
				if(!browser_num.containsKey(browserType)) {
					browser_num.put(browserType, 1);
				}else {
					int num = browser_num.get(browserType);
					browser_num.put(browserType, num+1);
				}
			}else {
				HashMap<String, Integer> browser_num = new HashMap<>();
				browser_num.put(browserType, 1);
				user_browser.put(username, browser_num);
			}
		}
		br.close();
		return user_browser;
	}

	//这个函数的输出(user_session_log)是文件result_log_user_session.txt中的内容
	private static TreeMap<String, ArrayList<String>> getLogErrorBySession(ArrayList<String> log_user) throws FileNotFoundException, IOException {
		//user_session_log存储的是一次会话的log错误日志,用于后面标记风险访问的
		TreeMap<String, ArrayList<String>> user_session_log = new TreeMap<>();
		String line = null;
		int len = log_user.size();
		for(int q=0;q<len;q++) {
			line = log_user.get(q);					
			String []datas = line.split("&&");
			String username = datas[0];
			String ip = datas[1];
			String date = datas[2];
			String timeStamp = date2TimeStamp(date);
			//没有这个键
			if(!user_session_log.containsKey(username+"_"+timeStamp)) {
				boolean flag = false;
				for(String user_time:user_session_log.keySet()) {
					String u = user_time.split("_")[0];
					String t = user_time.split("_")[1];
					//姓名相同了,在比较时间
					if(u.equals(username)) {
						//这两个时间戳的单位是秒,小于100说明是在100秒之内的
						if(Math.abs(Long.parseLong(t)-Long.parseLong(timeStamp))<100) {
							flag = true;
							ArrayList<String> data = user_session_log.get(user_time);
							if(data==null) {
								data = new ArrayList<>();
								for(int k=2;k<datas.length;k++) {
									data.add(datas[k]);								 
								}
								user_session_log.put(user_time, data);
							}else {
								for(int k=2;k<datas.length;k++) {
									data.add(datas[k]);								 
								}
								user_session_log.put(user_time, data);
							}
						}
					}
				}
				if(!flag) {
					ArrayList<String> list = new ArrayList<>();
					for(int k=2;k<datas.length;k++) {
						list.add(datas[k]);								 
					}
					user_session_log.put(username+"_"+timeStamp, list);
				}
				//含有这个键,追加数据
			}else {
				ArrayList<String> data = user_session_log.get(username+"_"+timeStamp);
				if(data!=null) {
					for(int k=2;k<datas.length;k++) {
						data.add(datas[k]);								 
					}
					user_session_log.put(username+"_"+timeStamp, data);
				}
			}
		}
		//这个函数的输出(user_session_log)是文件result_log_user_session.txt中的内容
		return user_session_log;
	}

	/**
	 * 获取所有的内网IP
	 * @throws Exception
	 */
	public static HashMap<String,ArrayList<String>> getALLIP(String dateStart,String dateEnd) throws Exception{
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,ArrayList<String>> IP_session = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String IP = obj.getString("IP");
			if(IP==null) {
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = sdf.parse(date);
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			if(dt.getTime()>end.getTime()||dt.getTime()<start.getTime()) {
				continue;
			}
			String timeStamp = date2TimeStamp(date);
			if(IP_session.containsKey(IP)) {
				//获取这个IP上登录过的session
				ArrayList<String> sessions = IP_session.get(IP);
				sessions.add(username+"_"+timeStamp);
				IP_session.put(IP, sessions);
			}else {
				ArrayList<String> sessions = new ArrayList<>();
				sessions.add(username+"_"+timeStamp);
				IP_session.put(IP, sessions);
			}
		}
		br.close();
		return IP_session;
	}


	/**
	 * 获取一个用户一定时间内的Ip地址数,
	 */
	public static HashMap<String,HashSet<String>> getuser_IPNum(String dateStart,String dateEnd) throws Exception{
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,HashSet<String>> user_IPNum = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String IP = obj.getString("IP");
			if(IP==null) {
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = sdf.parse(date);
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			if(dt.getTime()>end.getTime()||dt.getTime()<start.getTime()) {
				continue;
			}
			if(!user_IPNum.containsKey(username)) {
				HashSet<String> ips = new HashSet<>();
				ips.add(IP);
				user_IPNum.put(username, ips);
			}else {
				HashSet<String> ips = user_IPNum.get(username);
				ips.add(IP);
				user_IPNum.put(username, ips);
			}

		}
		br.close();
		return user_IPNum;
	}

	/**
	 * 获取用户一定时间内的浏览器数
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,HashSet<String>> getuser_BrowserNum(String dateStart,String dateEnd) throws Exception{
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,HashSet<String>> user_BrowserNum = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String IP = obj.getString("IP");
			String browserType = obj.getString("浏览器类型");
			if(IP==null) {
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = sdf.parse(date);
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			if(dt.getTime()>end.getTime()||dt.getTime()<start.getTime()) {
				continue;
			}
			if(!user_BrowserNum.containsKey(username)) {
				HashSet<String> browsers = new HashSet<>();
				browsers.add(browserType);
				user_BrowserNum.put(username, browsers);
			}else {
				HashSet<String> browsers = user_BrowserNum.get(username);
				browsers.add(browserType);
				user_BrowserNum.put(username, browsers);
			}

		}
		br.close();
		return user_BrowserNum;
	}

	/**
	 * 获取用户一定时间内的登录次数
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,HashSet<String>> getuser_LoginNum(String dateStart,String dateEnd) throws Exception{
		File file = new File("data\\data_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		HashMap<String,HashSet<String>> user_LoginNum = new HashMap<>();
		while((line = br.readLine())!=null) {
			//按照文本的顺序解析json字符串
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String message = obj.getString("message");
			if(message==null) {
				continue;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt = sdf.parse(date);
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			if(dt.getTime()>end.getTime()||dt.getTime()<start.getTime()) {
				continue;
			}
			if(!user_LoginNum.containsKey(username)) {
				HashSet<String> logins = new HashSet<>();
				logins.add(username+"_"+date+"_"+message);
				user_LoginNum.put(username, logins);
			}else {
				HashSet<String> logins = user_LoginNum.get(username);
				logins.add(username+"_"+date+"_"+message);
				user_LoginNum.put(username, logins);
			}

		}
		br.close();
		return user_LoginNum;
	}


	/**
	 * 获取用户一定时间内访问的风险/正常/未知次数
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,ArrayList<String>> getuser_riskNum(String dateStart,String dateEnd) throws Exception{
		//获取了所有会话的状态情况
		ArrayList<String> session_stats = getSessionState();
		HashMap<String,ArrayList<String>> user_status = new HashMap<>();
		for(String session_stat:session_stats) {
			//session_stat是wangyang@wis-eye.com_1539747195::正常访问
			String username = session_stat.split("_")[0];
			String temp = session_stat.split("_")[1];
			String timeStamp = temp.split("::")[0];
			String status = session_stat.split("::")[1];
			//添加上末尾的3个000
			timeStamp = timeStamp+"000";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//不在时间范围内
			if(Long.parseLong(timeStamp)<sdf.parse(dateStart).getTime()||Long.parseLong(timeStamp)>sdf.parse(dateEnd).getTime()) {
				continue;
			}
			if(!user_status.containsKey(username)) {
				ArrayList<String> sts = new ArrayList<String>();
				sts.add(status);
				user_status.put(username, sts);
			}else {
				ArrayList<String> sts = user_status.get(username);
				sts.add(status);
				user_status.put(username, sts);
			}

		}
		return user_status;
	}

	/** "2018-12-01 08:35:46","2018-12-22 23:59:59"
	 * 获取一段时间范围内的聚类分类的数据  user,login,ip,browser,normal,risk,unknown,class
	 * @throws Exception
	 */
	public static void getClassifyInfo(String dateStart,String dateEnd) throws Exception {

		HashMap<String, ArrayList<Integer>> data = new HashMap<>(); 

		HashMap<String,HashSet<String>> user_logins = getuser_LoginNum(dateStart,dateEnd);
		for(String user:user_logins.keySet()) {
			//System.out.println(user+"::"+user_logins.get(user).size());
			if(!data.containsKey(user)) {
				ArrayList<Integer> list = new ArrayList<>();
				list.add(user_logins.get(user).size());
				data.put(user, list);
			}else {
				ArrayList<Integer> list = data.get(user);
				list.add(user_logins.get(user).size());
				data.put(user, list);
			}
			//System.out.println(user+"::"+user_logins.get(user)));
		}
		//		System.out.println("------------------");
		HashMap<String,HashSet<String>> user_ipNum = getuser_IPNum(dateStart,dateEnd);
		for(String user:user_ipNum.keySet()) {
			//System.out.println(user+"::"+user_ipNum.get(user).size());
			if(!data.containsKey(user)) {
				ArrayList<Integer> list = new ArrayList<>();
				list.add(user_ipNum.get(user).size());
				data.put(user, list);
			}else {
				ArrayList<Integer> list = data.get(user);
				list.add(user_ipNum.get(user).size());
				data.put(user, list);
			}
			//System.out.println(user+"::"+user_ipNum.get(user));
		}
		//		System.out.println("------------------");		
		HashMap<String,HashSet<String>> user_browserNum = getuser_BrowserNum(dateStart,dateEnd);
		for(String user:user_browserNum.keySet()) {
			//System.out.println(user+"::"+user_browserNum.get(user).size());
			if(!data.containsKey(user)) {
				ArrayList<Integer> list = new ArrayList<>();
				list.add(user_browserNum.get(user).size());
				data.put(user, list);
			}else {
				ArrayList<Integer> list = data.get(user);
				list.add(user_browserNum.get(user).size());
				data.put(user, list);
			}
			//System.out.println(user+"::"+user_browserNum.get(user));
		}
		//		System.out.println("------------------");				
		HashMap<String,ArrayList<String>> user_status = getuser_riskNum(dateStart,dateEnd);
		for(String user:user_status.keySet()) {
			ArrayList<String> status = user_status.get(user);
			int normal=0,risk=0,unknow=0;
			for(int i=0;i<status.size();i++) {
				if(status.get(i).equals("正常访问")) {
					normal++;
				}else if(status.get(i).equals("风险访问")) {
					risk++;
				}else if(status.get(i).equals("未知访问")) {
					unknow++;
				}
			}
			//System.out.println(user+"::"+"正常访问:"+normal+"风险访问:"+risk+"未知访问"+unknow);
			if(!data.containsKey(user)) {
				ArrayList<Integer> list = new ArrayList<>();
				list.add(normal);
				list.add(risk);
				list.add(unknow);
				data.put(user, list);
			}else {
				ArrayList<Integer> list = data.get(user);
				list.add(normal);
				list.add(risk);
				list.add(unknow);
				data.put(user, list);
			}
			//System.out.println(user+"::"+user_status.get(user));
		}
		Set<String> users = data.keySet();
		HashMap<String, Integer> user_id = new HashMap<>();
		int k=0;
		for(String user:users) {
			if(user_id.containsKey(user)) {
				continue;
			}
			user_id.put(user, k++);
		}
		//将数据写入表格
		//		System.out.println("*******************");
		for(String user:data.keySet()) {
			//			System.out.println(data.get(user));
			ArrayList<Integer> res = data.get(user);
			String s = res.toString();
			s = s.replace("[", "");
			s = s.replace("]", "");
			if(user.contains("sim")) {
				System.out.println(user_id.get(user)+","+s+",f");				
			}else {
				System.out.println(user_id.get(user)+","+s+",z");	
			}
		}



	}


	/**
	 * 获取隐马尔可夫的数据,产生result_HMM.txt
	 */
	public static HashMap<String, String> getHMMData(String dateStart,String dateEnd) throws Exception{
		//获取所有会话状态
		ArrayList<String> session_status = getSessionState();
		HashMap<String, String> user_session_status = new HashMap<>();
		for(int i=0;i<session_status.size();i++) {
			String ut_sessionStat = session_status.get(i);
			String ut = ut_sessionStat.split("::")[0];
			String stat = ut_sessionStat.split("::")[1];
			user_session_status.put(ut, stat);
		}
		//获取所有会话活跃度值
		HashMap<String,Double> user_session_active = computeActvie(dateStart,dateEnd);
		ArrayList<Double> res = new ArrayList<>();
		for(String user_time:user_session_active.keySet()) {
			res.add(user_session_active.get(user_time));
		}
		//由大到小排序是为了定义阈值(3.25,1.47)
		Collections.sort(res, new C());
		double b1 = 0.0,b2=0.0;
		int size = res.size();
		//b1>b2
		b1 = res.get(size/3);
		b2 = res.get(size/3 + size/3);
		ArrayList<String> result_session = getResult_Session();
		int len = result_session.size();
		//在HMM中定义正常访问,异常访问;活跃,一般,不活跃
		HashMap<String, String> HMMData = new HashMap<>();
		String line = null;
		for(int q=0;q<len;q++){
			line = result_session.get(q);
			String username_time = line.split("--&&--")[0];
			String status = user_session_status.get(username_time);
			Double score = user_session_active.get(username_time);
			if(status==null||score==null) {
				continue;
			}
			String timeStamp =  username_time.split("_")[1];
			//添加上末尾的3个000
			timeStamp = timeStamp+"000";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			Long endTimestamp = end.getTime();
			Long startTimestamp = start.getTime();
			if(Long.parseLong(timeStamp)>endTimestamp||Long.parseLong(timeStamp)<startTimestamp) {
				continue;
			}
			if(status.contains("正常")) {
				if(score>b1) {
					HMMData.put(username_time, "正常_活跃");
				}else if(score>b2) {
					HMMData.put(username_time, "正常_一般");
				}else if(score>=0.0) {
					HMMData.put(username_time, "正常_不活跃");
				}
			}else {
				if(score>b1) {
					HMMData.put(username_time, "异常_活跃");
				}else if(score>b2) {
					HMMData.put(username_time, "异常_一般");
				}else if(score>=0.0) {
					HMMData.put(username_time, "异常_不活跃");
				}
			}
		}
		return HMMData;
	}


	/**
	 * 按用户来获取HMM的观测数据
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, ArrayList<String>> getHMMObserveDataByUser() throws Exception{
		HashMap<String, ArrayList<String>> user_session = new HashMap<>();
		HashMap<String, String> HMMData = getHMMData("2018-10-08 08:35:46","2019-12-22 23:59:59");
		for(String ut:HMMData.keySet()) {
			String user_timeStamp = ut;
			String username = ut.split("_")[0];
			String timeStamp = ut.split("_")[1];
			String stat = HMMData.get(ut);
			if(!user_session.containsKey(username)) {
				ArrayList<String> list = new ArrayList<>();
				list.add(user_timeStamp+"_"+stat);
				user_session.put(username, list);				
			}else {
				ArrayList<String> list = user_session.get(username);
				list.add(user_timeStamp+"_"+stat);
				user_session.put(username,list);
			}
		}		
		HashMap<String, ArrayList<String>> res = new HashMap<>();
		//定义6个观测变量    o0:正常,活跃   o1:正常,一般  o2:正常不活跃  o3:异常,活跃  o4:异常,一般  o5:异常,不活跃
		for(String username:user_session.keySet()) {
			ArrayList<String> sessions = user_session.get(username);
			for(String session:sessions) {
				String []datas = session.split("_");
				if("正常".equals(datas[2].trim())&&"活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("o0");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("o0");
						res.put(username, observes);
					}
				}else if("正常".equals(datas[2].trim())&&"一般".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("o1");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("o1");
						res.put(username, observes);
					}
				}else if("正常".equals(datas[2].trim())&&"不活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("o2");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("o2");
						res.put(username, observes);
					}
				}else if("异常".equals(datas[2].trim())&&"活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("o3");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("o3");
						res.put(username, observes);
					}
				}else if("异常".equals(datas[2].trim())&&"一般".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("o4");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("o4");
						res.put(username, observes);
					}
				}else if("异常".equals(datas[2].trim())&&"不活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("o5");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("o5");
						res.put(username, observes);
					}
				}
			}
		}
		for(String user:res.keySet()) {
			System.out.println(user+"::"+res.get(user));
		}
		return res;
	}

	/**
	 * 按用户来获取HMM的状态数据
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, ArrayList<String>> getHMMStatusByUser() throws Exception{

		HashMap<String, ArrayList<String>> user_session = new HashMap<>();
		File file = new File("data\\result_HMM.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line=br.readLine())!=null) {
			String user_timeStamp = line.split("::")[0];
			String username = user_timeStamp.split("_")[0];
			String timeStamp = user_timeStamp.split("_")[1];
			String data = line.split("::")[1];
			if(!user_session.containsKey(username)) {
				ArrayList<String> list = new ArrayList<>();
				list.add(user_timeStamp+"_"+data);
				user_session.put(username, list);				
			}else {
				ArrayList<String> list = user_session.get(username);
				list.add(user_timeStamp+"_"+data);
				user_session.put(username,list);
			}
		}
		HashMap<String, ArrayList<String>> res = new HashMap<>();
		//定义4个状态变量    s0:正常,活跃   s1:正常,不活跃    s2:异常,活跃  s3:异常,不活跃
		for(String username:user_session.keySet()) {
			ArrayList<String> sessions = user_session.get(username);
			for(String session:sessions) {
				String []datas = session.split("_");
				if("正常".equals(datas[2].trim())&&"活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("s0");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("s0");
						res.put(username, observes);
					}
				}else if("正常".equals(datas[2].trim())&&"一般".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("s0");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("s0");
						res.put(username, observes);
					}
				}else if("正常".equals(datas[2].trim())&&"不活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("s1");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("s1");
						res.put(username, observes);
					}
				}else if("异常".equals(datas[2].trim())&&"活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("s2");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("s2");
						res.put(username, observes);
					}
				}else if("异常".equals(datas[2].trim())&&"一般".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("s2");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("s2");
						res.put(username, observes);
					}
				}else if("异常".equals(datas[2].trim())&&"不活跃".equals(datas[3].trim())) {
					if(!res.containsKey(username)) {
						ArrayList<String> observes = new ArrayList<>();
						observes.add("s3");
						res.put(username, observes);
					}else {
						ArrayList<String> observes = res.get(username);
						observes.add("s3");
						res.put(username, observes);
					}
				}
			}
		}
		for(String user:res.keySet()) {
			System.out.println(user+"::"+res.get(user));
		}

		return res;
	}



	/**
	 * 计算这次会话用户的活跃度,都通过数据归一化
	 */
	public static HashMap<String,Double> computeActvie(String dateStart,String dateEnd) throws Exception{
		ArrayList<String> result_session = getResult_Session();
		HashMap<String,Double> user_active_socre = new HashMap<>();
		HashMap<String, ArrayList<String>> user_active = getDynamiData(result_session,dateStart,dateEnd);
		HashMap<String, ArrayList<String>> user_dynamic_allPage = getDynamicDataCombineAllPage(user_active);
		//计算最大值和最小值
		Double dmax1=Double.MIN_VALUE,dmax2=Double.MIN_VALUE,dmax3=Double.MIN_VALUE,dmax4=Double.MIN_VALUE;
		Double dmin1=Double.MAX_VALUE,dmin2=Double.MAX_VALUE,dmin3=Double.MAX_VALUE,dmin4=Double.MAX_VALUE;
		for(String user:user_dynamic_allPage.keySet()) {
			ArrayList<String> list = user_dynamic_allPage.get(user);
			//list的大小是4
			for(int i=0;i<list.size();i+=4) {
				if(dmax1<Double.parseDouble(list.get(i))) {
					dmax1 = Double.parseDouble(list.get(i));
				}
				if(dmax2<Double.parseDouble(list.get(i+1))) {
					dmax2 = Double.parseDouble(list.get(i+1));
				}
				if(dmax3<Double.parseDouble(list.get(i+2))) {
					dmax3 = Double.parseDouble(list.get(i+2));
				}
				if(dmax4<Double.parseDouble(list.get(i+3))) {
					dmax4 = Double.parseDouble(list.get(i+3));
				}
				if(dmin1>Double.parseDouble(list.get(i))) {
					dmin1 = Double.parseDouble(list.get(i));
				}
				if(dmin2>Double.parseDouble(list.get(i+1))) {
					dmin2 = Double.parseDouble(list.get(i+1));
				}
				if(dmin3>Double.parseDouble(list.get(i+2))) {
					dmin3 = Double.parseDouble(list.get(i+2));
				}
				if(dmin4>Double.parseDouble(list.get(i+3))) {
					dmin4 = Double.parseDouble(list.get(i+3));
				}
			}
		}

		for(String user_time:user_dynamic_allPage.keySet()) {
			ArrayList<String> list = user_dynamic_allPage.get(user_time);
			if(list==null|| list.size()==0) {
				user_active_socre.put(user_time, 0.0);
				continue;
			}
			double i1 = 0.0, i2 = 0.0, i3=0.0, i4=0.0;
			for(int i=0;i<list.size();i+=4) {
				String d1 = list.get(i);
				double num1 = Double.parseDouble(d1);
				i1 += (num1-dmin1)/(dmax1-dmin1);
				String d2 = list.get(i+1);
				double num2 = Double.parseDouble(d2);
				i2 += (num2-dmin2)/(dmax2-dmin2);
				String d3 = list.get(i+2);
				double num3 = Double.parseDouble(d3);
				i3 += (num3-dmin3)/(dmax3-dmin3);
				String d4 = list.get(i+3);
				double num4 = Double.parseDouble(d4);
				i4 += (num4-dmin4)/(dmax4-dmin4);
			}
			double score = i1+i2+i3+i4;
			user_active_socre.put(user_time, score);
		}

		return user_active_socre;
	}


	public static void prepareBrowserData() throws Exception{
		File file = new File("data\\Browserdata_all_paixu.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;

		while((line=br.readLine())!=null) {
			//			System.out.println(line);
			JSONObject obj = (JSONObject) JSONObject.parseObject(line, Feature.OrderedField);
			String username = obj.getString("username");
			String date = obj.getString("date");
			String session = obj.getString("session");
			session = session.replaceAll("\"", "");
			String tjRefer = obj.getString("tjRefer");
			String browserType = obj.getString("浏览器类型");
			String userAgent = obj.getString("浏览器属性信息");
			String pluginsInfo = obj.getString("插件的名称");
			String W = obj.getString("屏幕分辨率宽度");
			String H = obj.getString("屏幕分辨率高度");
			String clientIp = obj.getString("clientIP");
			String clientName = obj.getString("clientName");
			String url = obj.getString("url");
			String ip = obj.getString("IP");
			String sql = "insert into browserdata (username,date_,session_,tjRefer,browserType,userAgent,pluginsInfo,W,H,clientIp,clientName,url,ip) "
					+ "values ( '"+username+"','"+date+"','"+session+"','"+tjRefer+"','"+browserType+"','"+userAgent+"','"
					+"','"+pluginsInfo+"','"+W+"','"+H+"','"+clientIp+"','"+clientName+"','"+url+"','"+ip+"')";
			System.out.println(sql);
		}

		br.close();
	}


	public static ArrayList<String> getSessionState() throws Exception {
		ArrayList<String> result_session = getResult_Session();
		ArrayList<String> log_user = getUser_Log();

		//获取所有的会话状态,最后在截取,judgeSession函数是产生result_session_risk.txt文件的
		HashMap<String, String> session_status = judgeSession(log_user,result_session,"2018-10-09 01:00:00","2020-01-14 20:37:44");
		ArrayList<String> sesstion_states = new ArrayList<>();
		for(String session:session_status.keySet()) {
			//liushenrong@wis-eye.com_1539073495::正常访问
			sesstion_states.add(session+"::"+session_status.get(session));
		}
		return sesstion_states;

	}

	private static ArrayList<String> getUser_Log() throws Exception {
		//统计了所有数据,parseLogDate函数产生的是result_log.txt文件
		ArrayList<String> list = parseLogDate();
		ArrayList<String> logs = new ArrayList<>();
		for(String s:list) {
			logs.add(s);
		}
		//统计了所有数据,user_log函数是将log和用户名对应起来的,产生result_log_user.txt文件
		ArrayList<String> log_user = user_log(logs);
		return log_user;
	}

	private static ArrayList<String> getResult_Session() throws Exception {
		//统计了所有数据,parseCollecedData函数产生的是result_collectData.txt文件
		HashMap<String,ArrayList<String>> user_data = parseCollecedData();
		ArrayList<String> collectDatas = new ArrayList<>();
		for(String username:user_data.keySet()) {
			ArrayList<String> datas = user_data.get(username);
			for(int i=0;i<datas.size();i++) {
				collectDatas.add(username+"&&"+datas.get(i));
			}
		}
		//统计了所有数据,huihua函数是产生result_session.txt文件的
		TreeMap<String, ArrayList<String>> user_session = huihua(collectDatas);
		ArrayList<String> result_session = new ArrayList<>();				
		for(String user_sess:user_session.keySet()) {
			result_session.add(user_sess+"--&&--"+user_session.get(user_sess));
		}
		return result_session;
	}

	public static HashMap<String,ArrayList<String>> getDynamiData(ArrayList<String> result_session,String dateStart,String dateEnd) 
			throws Exception {		
		String line = null;
		HashMap<String,ArrayList<String>> user_active = new HashMap<>();
		int len = result_session.size();
		for(int q=0;q<len;q++){
			line = result_session.get(q);
			String user_time =  line.split("--&&--")[0];
			String timeStamp = user_time.split("_")[1];
			timeStamp = timeStamp+"000";
			String data = line.split("--&&--")[1];
			data = data.replace("[", "");
			data = data.replace("]", "");
			String []ds = data.split(",");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = sdf.parse(dateStart);
			Date end = sdf.parse(dateEnd);
			//不在时间范围内
			Long endTimestamp = end.getTime();
			Long startTimestamp = start.getTime();
			if(Long.parseLong(timeStamp)>endTimestamp||Long.parseLong(timeStamp)<startTimestamp) {
				continue;
			}
			boolean flag = true;
			//从后向前,每次读取4个数据
			for(int i=ds.length-1;i>=0;i-=4) {
				try {
					//检测数据转换是否异常
					double d = Double.parseDouble(ds[i]);
					if(!user_active.containsKey(user_time)) {
						ArrayList<String> list = new ArrayList<>();
						list.add(ds[i-3]);
						list.add(ds[i-2]);
						list.add(ds[i-1]);
						list.add(ds[i]);
						user_active.put(user_time, list);
					}else {
						ArrayList<String> list = user_active.get(user_time);
						list.add(ds[i-3]);
						list.add(ds[i-2]);
						list.add(ds[i-1]);
						list.add(ds[i]);
						user_active.put(user_time, list);
					}
				} catch (Exception e) {
					flag = false;
					if(i==ds.length-1) {
						ArrayList<String> list = new ArrayList<>();
						list.add("0.0");
						list.add("0.0");
						list.add("0.0");
						list.add("0.0");
						user_active.put(user_time, list);
					}
				}
				if(!flag) {
					break;
				}
			}
		}
		return user_active;
	}


	public static void getClusterInfo(String dateStart,String dateEnd) throws Exception{
		//存储最后的结果
		HashMap<String, ArrayList<String>> data = new HashMap<>(); 
		//统计了所有数据,parseCollecedData函数产生的是result_collectData.txt文件
		HashMap<String,ArrayList<String>> user_data = parseCollecedData();
		ArrayList<String> collectDatas = new ArrayList<>();
		for(String username:user_data.keySet()) {
			ArrayList<String> datas = user_data.get(username);
			for(int i=0;i<datas.size();i++) {
				collectDatas.add(username+"&&"+datas.get(i));
			}
		}
		//统计了所有数据,huihua函数是产生result_session.txt文件的
		TreeMap<String, ArrayList<String>> user_session = huihua(collectDatas);
		ArrayList<String> result_session = new ArrayList<>();				
		for(String user_sess:user_session.keySet()) {
			result_session.add(user_sess+"--&&--"+user_session.get(user_sess));
		}
		HashMap<String,ArrayList<String>> user_dynamic = getDynamiData(result_session,dateStart,dateEnd);
		HashMap<String,ArrayList<ArrayList<String>>> user_dynamic_allSession = new HashMap<>();
		HashMap<String, ArrayList<String>> user_dynamic_add = getDynamicDataCombineAllPage(user_dynamic);
		for(String ut:user_dynamic_add.keySet()) {
			String username = ut.split("_")[0];
			ArrayList<ArrayList<String>> subdata = user_dynamic_allSession.get(username);
			if(subdata==null) {
				subdata = new ArrayList<>();
				subdata.add(user_dynamic_add.get(ut));
				user_dynamic_allSession.put(username, subdata);
			}else {
				subdata.add(user_dynamic_add.get(ut));
			}
		}
		//获取kmax和kmin
		int kmin = Integer.MAX_VALUE;
		int kmax = Integer.MIN_VALUE;
		for(String user:user_dynamic_allSession.keySet()) {
			ArrayList<ArrayList<String>> sessions = user_dynamic_allSession.get(user);
			if(kmin>sessions.size()) {
				kmin = sessions.size();
			}
			if(kmax<sessions.size()) {
				kmax = sessions.size();
			}
		}
		for(String user:user_dynamic_allSession.keySet()) {
			ArrayList<ArrayList<String>> sessions = user_dynamic_allSession.get(user);
			int len = sessions.size();
			int d1 = 0;int d2 = 0;int d3 = 0;double d4 = 0.0;
			for(ArrayList<String> list:sessions) {
				d1 += Integer.parseInt(list.get(0).trim());
				d2 += Integer.parseInt(list.get(1).trim());
				d3 += Integer.parseInt(list.get(2).trim());
				d4 += Double.parseDouble(list.get(3).trim());
			}
			//不能乘以系数K了
			//			double k = (double)(len-kmin)/(kmax-kmin);
			//			if(len==kmin) {
			//				k = 1.0/(kmax-kmin);
			//			}
			DecimalFormat df = new DecimalFormat( "0.00");  
			String click = String.valueOf(df.format((float)(d1)/len));
			String wheel = String.valueOf(df.format((float)(d2)/len));
			String key = String.valueOf(df.format((float)(d3)/len));
			String time = String.valueOf(df.format((d4/len)));
			ArrayList<String> res = new ArrayList<>();
			res.add(click);res.add(wheel);res.add(key);res.add(time);
			data.put(user, res);
		}

		HashMap<String,HashSet<String>> user_logins = getuser_LoginNum(dateStart,dateEnd);
		for(String user:user_logins.keySet()) {
			if(!data.containsKey(user)) {
				ArrayList<String> list = new ArrayList<>();
				list.add(String.valueOf(user_logins.get(user).size()));
				data.put(user, list);
			}else {
				ArrayList<String> list = data.get(user);
				list.add(String.valueOf(user_logins.get(user).size()));
				data.put(user, list);
			}
		}

		//将数据写入表格
		for(String user:data.keySet()) {
			ArrayList<String> res = data.get(user);
			String s = res.toString();
			s = s.replace("[", "");
			s = s.replace("]", "");
			System.out.println(user+","+s.toString());
		}

	}

	/**
	 * 将依次会话中多个页面中的动态数据整合到一起
	 * @param user_dynamic
	 * @return
	 */
	private static HashMap<String, ArrayList<String>> getDynamicDataCombineAllPage(
			HashMap<String, ArrayList<String>> user_dynamic) {
		HashMap<String,ArrayList<String>> user_dynamic_add = new HashMap<>();
		for(String ut:user_dynamic.keySet()) {
			ArrayList<String> dynamicdata= user_dynamic.get(ut);
			if(dynamicdata.size()==4) {
				if(dynamicdata.get(0).equals("0.0")&&dynamicdata.get(1).equals("0.0")
						&&dynamicdata.get(2).equals("0.0")&&dynamicdata.get(3).equals("0.0")) {
					continue;
				}else {
					user_dynamic_add.put(ut, dynamicdata);
				}
			}else {
				ArrayList<String> dyn = new ArrayList<>();
				int d1 = 0;int d2 = 0;int d3 = 0;double d4 = 0.0;
				for(int i=0;i<dynamicdata.size();i+=4) {
					d1 += Integer.parseInt(dynamicdata.get(i).trim());
					d2 += Integer.parseInt(dynamicdata.get(i+1).trim());
					d3 += Integer.parseInt(dynamicdata.get(i+2).trim());
					d4 += Double.parseDouble(dynamicdata.get(i+3).trim());
				}
				dyn.add(String.valueOf(d1));dyn.add(String.valueOf(d2));
				dyn.add(String.valueOf(d3));dyn.add(String.valueOf(d4));
				user_dynamic_add.put(ut,dyn);
			}
		}
		return user_dynamic_add;
	}


	public static void main(String[] args) throws Exception {



		getClassifyInfo("2018-10-08 01:35:46","2018-10-15 23:59:59");

//		getClusterInfo("2019-02-23 01:35:46","2019-04-15 23:59:59");

//				HashMap<String,Double> user_active = computeActvie("2018-10-08 08:35:46","2019-12-22 23:59:59");
//				ArrayList<Double> res = new ArrayList<>();
//				for(String user_time:user_active.keySet()) {
//		//			System.out.println(user_time+"::"+user_active.get(user_time));
//					res.add(user_active.get(user_time));
//				}
		//		


//				HashMap<String, String> HMMData = getHMMData("2018-10-08 08:35:46","2019-12-22 23:59:59");
//				for(String user_time:HMMData.keySet()) {
//					System.out.println(user_time+"::"+HMMData.get(user_time));
//				}
//			getHMMObserveDataByUser();

		//		getHMMStatusByUser();
	}

}

class C1 implements Comparator<Double>{

	public int compare(Double o1, Double o2) {
		if(o1<o2) {
			return 1;
		}else if(o1>o2) {
			return -1;
		}
		return 0;
	}

}
