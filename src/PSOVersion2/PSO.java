package PSOVersion2;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

class PSO
{
	private Agent[] agent;
	private final int iStep = 1000; //迭代次数
	private double M_dBestFitness; //PSO所有粒子的全局极值
	private int m_iTempPos; //记录粒子全局最优值对应的下标

	
	//构造函数,调用readAgent函数，将数据集读入
	public PSO() {}
	

	public void readfile() throws IOException
	{
		//读取数据
		Agent.readAgent();
		//获取最大最小值
		Agent.initMaxMin();
		//数据归一化
//		Agent.standardization();
	}

	//创建粒子
	public void createpso()
	{
		//设置粒子群的全局最优极值
		M_dBestFitness = Float.MAX_VALUE;
		agent = new Agent[Agent.iPOSNum];
		//创建iPosnum个粒子的数组
		for(int i =0;i < Agent.iPOSNum;i++)
		{ 
			agent[i] = new Agent();
			agent[i].agentinitialize();
		}
	}

	public void Initialize()
	{
		//计算每个粒子的适应度的值
		for(int i = 0;i < Agent.iPOSNum;i++)
		{
			agent[i].m_dBestfitness = Float.MAX_VALUE; //将每个粒子的适应度初始为10000
			agent[i].UpdateFitness();
		}
	}

	public void Search()
	{
		int k = 0;
		while(k < iStep) //1000
		{
			m_iTempPos = 99; //记录粒子全局最优值对应的下标
			//对所有粒子的局部极值进行比较，找到全局极值
			for(int i =0; i< Agent.iPOSNum;i++)
			{
				//因为聚类所以是距离越小越好，找到最优极值，并记录相应的下标
				if(agent[i].m_dBestfitness < M_dBestFitness)
				{
					M_dBestFitness = agent[i].m_dBestfitness;
					m_iTempPos = i;
				}
			}
			//如果找到了全局极值，则更新全局极值，通过前面找到的下标
			if(m_iTempPos != 99)
			{
				//群体极值更新
				for(int i =0;i < Agent.iAgentDim*Agent.ikmeans;i++)
				{
					try {
						Agent.gbest[i] = agent[m_iTempPos].dpbest[i];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//更新所有粒子的速度和位置，计算一个粒子就更新一次
			for(int i = 0; i < Agent.iPOSNum;i++)
			{
				agent[i].UpdatePos();
				//计算所有粒子的适应度值
				agent[i].UpdateFitness();
			}
			k++;
			DecimalFormat df = new DecimalFormat( "0");  
//			System.out.println("迭代次数:"+k+"全局最优适应度值为:"+df.format(M_dBestFitness));
		}
		//循环结束
		for(int i = 0;i < Agent.iAgentDim*Agent.ikmeans;i++)
		{
			if(i==0) {
				System.out.print("[[");
			}
			if(i!=0&&i%Agent.iAgentDim==0&&i!=Agent.iAgentDim*Agent.ikmeans-1) {
				System.out.print("],[");
			}
			System.out.print(Agent.gbest[i] + ",");
			if(i==Agent.iAgentDim*Agent.ikmeans-1) {
				System.out.print("]],");
			}
		}
		System.out.println();
	}
	
	
}