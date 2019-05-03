package PSOVersion2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class Agent
{
    
	public static int iPOSNum = 20;      //粒子个数
	public static int iAgentDim = 19; //粒子维度
	public static int ikmeans = 2; //聚类中心数
	//对粒子的速度和位置进行约束,也可以进行数据归一化使用
	private static double []MAX = new double[iAgentDim];
	private static double []MIN = new double[iAgentDim];
	private static double []STANDMAX = new double[iAgentDim];
	private static double []STANDMIN = new double[iAgentDim];
	public static double[] gbest = new double[iAgentDim*ikmeans]; 
	//result中每一行，又是一个list数组
	public static List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
	//权重因子的取值非常重要,取值大了的话,适应度值可能会出现抖动的情况,实验发现取0.5比较合适,可以试一试更小的情形或者稍大的情形
	private final double w = 0.75;
	private final double c1= 1.0;
	private final double c2 = 1.0;
	public double[] dpos = new double[iAgentDim*ikmeans]; //粒子的位置
	public double[] dpbest = new double[iAgentDim*ikmeans]; //粒子本身的最优位置
	public double[] dv = new double[iAgentDim*ikmeans]; //粒子的速度
	private double m_dFitness=0;  //当前计算的粒子的解(判断是否更优)
	public double m_dBestfitness; //m_dBestfitness 粒子的最优解，适应度
	private Random random = new Random();
	
	
	
	/**
	 * 这个函数要特别的注意,因为不同的数据集里面的格式是不同的,在读取数据的时候要特别单独的处理一下
	 * @throws IOException
	 */
	public static void readAgent() throws IOException
	{
//		File file=new File("D://PSOdata//HTRU2//HTRU_2.csv");
//		File file=new File("D:\\PSOdata\\seeds\\seeds.txt");
//		File file=new File("D:\\PSOdata\\iris\\iris.txt");
//		File file=new File("D://PSOdata//magic//magic04.csv");
//		File file=new File("D://PSOdata//wine//wine.txt");
//		File file=new File("D://PSOdata//user//cluster_10.txt");
		File file=new File("D://PSOdata//messidor_features//messidor_features.txt");
		BufferedReader br=new BufferedReader(new FileReader(file));
		String s=null;
		while ((s=br.readLine())!=null)
		{
			String record = s.toString(); 
			String[] fields = record.split(","); 
			List<Double> tmplist = new ArrayList<Double>();
			//掐头去尾
			for (int i = 0; i < fields.length-1; ++i)
			{ 
				tmplist.add(Double.parseDouble(fields[i].trim())); 
			} 
			//System.out.println(tmplist);
			result.add((ArrayList<Double>) tmplist);  
		}
		br.close();
	}
	
	public static void standardization() {
		int len = result.size();
		for(int i=0;i<len;i++) {
			ArrayList<Double> list = result.get(i);
			for(int j=0;j<list.size();j++){
				double number = list.get(j);
				double standNum = (number-MIN[j])/(MAX[j]-MIN[j]);
				list.set(j, standNum);
			}
		}
	}
	
	public static void initSTANDMaxMin() {
		int len = result.size();
		int size = result.get(0).size();	
		for(int q=0;q<STANDMAX.length;q++) {
			STANDMAX[q] = Float.MIN_VALUE;
		}
		for(int q=0;q<MIN.length;q++) {
			STANDMIN[q] = Float.MAX_VALUE;
		}
		for(int i=0;i<len;i++) {
			ArrayList<Double> list = result.get(i);
			for(int j=0;j<size;j++) {
				double number = list.get(j);
				if(STANDMAX[j]<number) {
					STANDMAX[j]=number;
				}
				if(STANDMIN[j]>number) {
					STANDMIN[j]=number;
				}
			}
		}
	}
	
	
	public static void initMaxMin() {
		int len = result.size();
		int size = result.get(0).size();	
		for(int q=0;q<MAX.length;q++) {
			MAX[q] = Float.MIN_VALUE;
		}
		for(int q=0;q<MIN.length;q++) {
			MIN[q] = Float.MAX_VALUE;
		}
		for(int i=0;i<len;i++) {
			ArrayList<Double> list = result.get(i);
			for(int j=0;j<size;j++) {
				double number = list.get(j);
				if(MAX[j]<number) {
					MAX[j]=number;
				}
				if(MIN[j]>number) {
					MIN[j]=number;
				}
			}
		}
	}

	//对粒子的位置和速度进行初始化
	public void agentinitialize()
	{
		//下面就是随机选取两个点作为初始的聚类中心
		//产生俩个不同的随机数,范围在[0,size-1]
		Set<Integer> set = new HashSet<Integer>();
		//以系统时间为种子
		Random ran = new Random();
		//存放结果的数组
		int[] results = new int[ikmeans];
		for (int i = 0; i < ikmeans; i++) 
		{
			//产生一个范围小于result.size()的数。
			int temp = ran.nextInt(result.size());
			//若此数已存在，则添加失败
			boolean flag=set.add(temp);
			if (flag) 
			{
				results[i] = temp;
			}else
			{
				--i;//这次不算，重头来过
			}
		}
		//根据随机挑选的位置作为初始的聚类中心
		int k=0;
		for(int i = 0; i < results.length; i++) 
		{
			//result中初始值是读取的文本中的数据,即某些样本点
			for (int j = 0; j < result.get(results[i]).size(); j++)
			{
				dpos[k]=result.get(results[i]).get(j);
				dv[k] = dpos[k];   //速度也是这样初始化的
				dpbest[k] = dpos[k];  //最优位置初始化
				k++;
			}
		}
	}

	public void UpdateFitness(){		
		//centers存放聚类中心
		ArrayList<double[]> centers = new ArrayList<>();
		for(int k=0;k<ikmeans;k++) {
			double [] center=new double[iAgentDim];
			for (int i = 0; i < center.length; i++)
			{
				center[i]=dpos[i+k*center.length];
			}
			centers.add(center);
		}
		//不要忘记重新初始化为0,否则永远不会更新
		m_dFitness = 0.0;
		//计算适应度函数的值，这里其实就是距离，计算每个数据点,i对应着样本点
		int num= result.size();
		for (int i = 0; i <num; i++){
			double  m_dFitnessk = Double.MAX_VALUE;
			double []distance = new double[centers.size()];		
			//计算该数据点到多有聚类中心的距离
			for(int k=0;k<centers.size();k++) {
				//计算当前点result.get(i)到每个中心的距离.这层遍历是粒子的维度
				for (int j = 0; j < result.get(i).size(); j++){
					distance[k] += Math.pow(result.get(i).get(j)-centers.get(k)[j], 2) ;				
				}
			}
			//选出最近的距离
			for(int k=0;k<distance.length;k++) {
				if(m_dFitnessk>distance[k]) {
					m_dFitnessk=distance[k];
				}
			}
			//该粒子的适应度(距离在累加)
			m_dFitness += m_dFitnessk;
		}
		//如果当前计算的适应度更优，则替换当前粒子的最优解，而且把最优位置也要记录
		if(m_dFitness < m_dBestfitness)
		{
			m_dBestfitness = m_dFitness;
			for(int i = 0; i < iAgentDim*ikmeans; i++)
			{
				//更新个体极值
				dpbest[i] = dpos[i];
			}
		}
	}

	//更新粒子的速度和位置,这里一定要有约束限制,不然粒子的适应度值可能会非常大
	public void UpdatePos()
	{		
		for(int i=0;i<ikmeans;i++) {
			for(int j=0;j<iAgentDim;j++) {
				int k = i*iAgentDim+j;
				dv[k] = w * dv[k] + c1 * random.nextDouble() * (dpbest[k] - dpos[k]) 
						+ c2 * random.nextDouble() * ( gbest[k] - dpos[k]);
				dpos[k] = dpos[k] + dv[k];
				//下面的坐标对比时没有问题的,k和j
				if(dpos[k]>MAX[j]) {
					dpos[k]=MAX[j];
				}
				if(dpos[k]<MIN[j]) {
					dpos[k]=MIN[j];
				}
//				if(dpos[k]>STANDMAX[j]) {
//					dpos[k]=STANDMAX[j];
//				}
//				if(dpos[k]<STANDMIN[j]) {
//					dpos[k]=STANDMIN[j];
//				}
			}
		}
	
	}

	public double getM_dFitness() {
		return m_dFitness;
	}

	public void setM_dFitness(double m_dFitness) {
		this.m_dFitness = m_dFitness;
	}

	
	
}



