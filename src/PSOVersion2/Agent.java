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
	
	public static int iPOSNum = 20; //���Ӹ���
	public static int iAgentDim = 7; //����ά��
	public static int ikmeans=2; //����������
	private final double w = 0.9;
	private final double c1= 1.49445;
	private final double c2 = 1.49445;
	public double[] dpos = new double[iAgentDim*ikmeans]; //���ӵ�λ��
	public double[] dpbest = new double[iAgentDim*ikmeans]; //���ӱ��������λ��
	public double[] dv = new double[iAgentDim*ikmeans]; //���ӵ��ٶ�
	private double m_dFitness=0; 
	public double m_dBestfitness; //m_dBestfitness ���ӱ�������Ž⣬��Ӧ��
	private Random random = new Random();
	public static double[] gbest = new double[iAgentDim*ikmeans]; 
	//result��ÿһ�У�����һ��list����
	public static List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
	//������һЩԼ��
	private float VMAX = 0.2f;
	private float VMIN = -0.2f;
	private float popmax = 1.0f;
	private float popmin = 0.0f;
	//���ļ��ж�ȡ���ݼ�
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

	//�����ӵ�λ�ú��ٶȽ��г�ʼ��
	public void agentinitialize()
	{
		//����������ѡȡ��������Ϊ��ʼ�ľ�������
		//����������ͬ�������,��Χ��[0,size-1]
		Set<Integer> set = new HashSet<Integer>();
		//��ϵͳʱ��Ϊ����
		Random ran = new Random();
		//��Ž��������
		int[] results = new int[ikmeans];
		for (int i = 0; i < ikmeans; i++) 
		{
			//����һ����ΧС��result.size()������
			int temp = ran.nextInt(result.size());
			//�������Ѵ��ڣ������ʧ��
			boolean flag=set.add(temp);
			if (flag) 
			{
				results[i] = temp;
			}else
			{
				--i;//��β��㣬��ͷ����
			}
		}
		//���������ѡ������λ����Ϊ��ʼ�ľ�������
		int k=0;
		for(int i = 0; i < results.length; i++) 
		{
			for (int j = 0; j < result.get(results[i]).size(); j++)
			{
				dpos[k]=result.get(results[i]).get(j);
				dv[k] = dpos[k];   //�ٶ�Ҳ��������ʼ����
				dpbest[k] = dpos[k];  //����λ�ó�ʼ��
				k++;
			}
		}
	}

	public void UpdateFitness()
	{
		//�˴������������м����������ĵĻ����ͱȽ����������Ի��ǻع鵽������������
		//��������ֱ���������������
		double [] k1=new double[iAgentDim];
		double [] k2=new double[iAgentDim];
		//k1,k2�ֱ������Ĵص���Ӧ��ֵ
		double m_dFitnessk1=0;
		double m_dFitnessk2=0;
		for (int i = 0; i < k1.length; i++)
		{
			k1[i]=dpos[i];
		}
		for (int i = 0; i < k2.length; i++)
		{
			k2[i]=dpos[i+k1.length];
		}
		//������Ӧ�Ⱥ�����ֵ��������ʵ���Ǿ���
		for (int i = 0; i <result.size(); i++) 
		{
			double disk1=0; //һ����ֱ�k1��k2�ľ��룬û�п�����
			double disk2=0;
			
			//���㵱ǰ��result.get(i)��ÿ�����ĵľ���
			for (int j = 0; j < result.get(i).size(); j++)
			{

				disk1+=Math.pow(result.get(i).get(j)-k1[j], 2) ;
				disk2+=Math.pow(result.get(i).get(j)-k2[j], 2) ;
				//��������k1�ȽϽ���������k1
				if(disk1<=disk2)
				{
					m_dFitnessk1+=disk1;
				}
				// ��������k2�ȽϽ���������k2
				else
				{
					m_dFitnessk2+=disk2;
				}
			}
		}
		m_dFitness = m_dFitnessk1+m_dFitnessk2; //������Ⱥ������Ӧ��
		//�����ǰ�������Ӧ�ȸ��ţ����滻�����Ұ�����λ��ҲҪ��¼
		if(m_dFitness < m_dBestfitness)
		{
			m_dBestfitness = m_dFitness;
			for(int i = 0; i < iAgentDim*ikmeans; i++)
			{
				//���¸��弫ֵ
				dpbest[i] = dpos[i];
			}

		}
//		System.out.println(m_dBestfitness);
	}

	//�������ӵ��ٶȺ�λ��
	public void UpdatePos()
	{
		//��û����Լ���أ�������
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



