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

	public static int iPOSNum = 20; //粒子个数
	public static int iAgentDim = 7; //粒子维度
	public static int ikmeans=3; //聚类中心数
	public static double[] gbest = new double[iAgentDim*ikmeans]; 
	//result中每一行，又是一个list数组
	public static List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
	private final double w = 0.9;
	private final double c1= 1.49445;
	private final double c2 = 1.49445;
	public double[] dpos = new double[iAgentDim*ikmeans]; //粒子的位置
	public double[] dpbest = new double[iAgentDim*ikmeans]; //粒子本身的最优位置
	public double[] dv = new double[iAgentDim*ikmeans]; //粒子的速度
	private double m_dFitness=0; 
	public double m_dBestfitness; //m_dBestfitness 粒子本身的最优解，适应度
	private Random random = new Random();
	//下面是一些约束
	private float VMAX = 0.2f;
	private float VMIN = -0.2f;
	private float popmax = 1.0f;
	private float popmin = 0.0f;
	//从文件中读取数据集
	public void readAgent() throws IOException
	{
		File file=new File("D://cluster.txt");
		BufferedReader br=new BufferedReader(new FileReader(file));
		String s=null;
		while ((s=br.readLine())!=null)
		{
			String record = s.toString(); 
			String[] fields = record.split(" "); 
			List<Double> tmplist = new ArrayList<Double>(); 
			for (int i = 0; i < fields.length; ++i)
			{ 
				tmplist.add(Double.parseDouble(fields[i])); 
			} 
			result.add((ArrayList<Double>) tmplist);  
		}
		br.close();
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
		//根据随机挑选的两个位置作为初始的聚类中心
		int k=0;
		for(int i = 0; i < results.length; i++) 
		{
			for (int j = 0; j < result.get(results[i]).size(); j++)
			{
				dpos[k]=result.get(results[i]).get(j);
				dv[k] = dpos[k];   //速度也是这样初始化的
				dpbest[k] = dpos[k];  //最优位置初始化
				k++;
			}
		}
	}

	public void UpdateFitness()
	{
		ArrayList<double[]> centers = new ArrayList<>();
		for(int k=0;k<ikmeans;k++) {
			double [] center=new double[iAgentDim];
			for (int i = 0; i < center.length; i++)
			{
				center[i]=dpos[i+k*center.length];
			}
			centers.add(center);
		}
		//计算适应度函数的值，这里其实就是距离，计算每个数据点
		for (int i = 0; i <result.size(); i++){
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
			//该粒子的适应度
			m_dFitness += m_dFitnessk;
		}
		//如果当前计算的适应度更优，则替换，而且把最优位置也要记录
		if(m_dFitness < m_dBestfitness)
		{
			m_dBestfitness = m_dFitness;
			for(int i = 0; i < iAgentDim*ikmeans; i++)
			{
				//更新个体极值
				dpbest[i] = dpos[i];
			}
		}
//		System.out.println(m_dBestfitness);
	}

	//更新粒子的速度和位置
	public void UpdatePos()
	{
		//还没加入约束呢！！！！
		for(int i = 0;i < iAgentDim*ikmeans;i++)
		{
			dv[i] = w * dv[i] + c1 * random.nextDouble() * (dpbest[i] - dpos[i]) + c2 * random.nextDouble() * ( gbest[i] - dpos[i]);
			//			if(dv[i]>VMAX) {
			//				dv[i]=VMAX;
			//			}
			//			if(dv[i]<VMIN) {
			//				dv[i]=VMIN;
			//			}
			dpos[i] = dpos[i] + dv[i];
			//			if(dpos[i]>popmax) {
			//				dpos[i]=popmax;
			//			}
			//			if(dpos[i]<popmin) {
			//				dpos[i]=popmin;
			//			}
		}
		//		for(int i=0;i<dv.length;i++) {
		//			System.out.print(dv[i]+" ");			
		//		}
		//		System.out.println();
		//		for(int i=0;i<dpos.length;i++) {
		//			System.out.print(dpos[i]+" ");			
		//		}
		//		System.out.println();
	}

}



