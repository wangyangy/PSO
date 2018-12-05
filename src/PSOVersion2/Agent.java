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
    
	public static int iPOSNum = 20;      //���Ӹ���
	public static int iAgentDim = 4; //����ά��
	public static int ikmeans=3; //����������
	public static double[] gbest = new double[iAgentDim*ikmeans]; 
	//result��ÿһ�У�����һ��list����
	public static List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
	private final double w = 1;
	private final double c1= 1.49445;
	private final double c2 = 1.49445;
	public double[] dpos = new double[iAgentDim*ikmeans]; //���ӵ�λ��
	public double[] dpbest = new double[iAgentDim*ikmeans]; //���ӱ��������λ��
	public double[] dv = new double[iAgentDim*ikmeans]; //���ӵ��ٶ�
	private double m_dFitness=0; 
	public double m_dBestfitness; //m_dBestfitness ���ӱ�������Ž⣬��Ӧ��
	private Random random = new Random();
	//������һЩԼ��
	private float VMAX = 0.2f;
	private float VMIN = -0.2f;
	private float popmax = 1.0f;
	private float popmin = 0.0f;
	
	//���ļ��ж�ȡ���ݼ�
//	public void readAgent() throws IOException
//	{
//		File file=new File("D://cluster.txt");
//		BufferedReader br=new BufferedReader(new FileReader(file));
//		String s=null;
//		while ((s=br.readLine())!=null)
//		{
//			String record = s.toString(); 
//			String[] fields = record.split(" "); 
//			List<Double> tmplist = new ArrayList<Double>(); 
//			for (int i = 0; i < fields.length; ++i)
//			{ 
//				tmplist.add(Double.parseDouble(fields[i])); 
//			} 
//			result.add((ArrayList<Double>) tmplist);  
//		}
//		br.close();
//	}
	
	//�������Ҫ�ر��ע��,��Ϊ��ͬ�����ݼ�����ĸ�ʽ�ǲ�ͬ��,�ڶ�ȡ���ݵ�ʱ��Ҫ�ر𵥶��Ĵ���һ��
	public void readAgent() throws IOException
	{
		File file=new File("D://data//iris_normalize.txt");
		BufferedReader br=new BufferedReader(new FileReader(file));
		String s=null;
		while ((s=br.readLine())!=null)
		{
			String record = s.toString(); 
			String[] fields = record.split(","); 
			List<Double> tmplist = new ArrayList<Double>();
			//��ͷȥβ
			for (int i = 0; i < fields.length-1; ++i)
			{ 
				tmplist.add(Double.parseDouble(fields[i])); 
			} 
//			System.out.println(tmplist);
			result.add((ArrayList<Double>) tmplist);  
		}
//		for(int i =0;i<result.size();i++) {
//			System.out.println(result.get(i));
//		}
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
		//���������ѡ��λ����Ϊ��ʼ�ľ�������
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
		ArrayList<double[]> centers = new ArrayList<>();
		for(int k=0;k<ikmeans;k++) {
			double [] center=new double[iAgentDim];
			for (int i = 0; i < center.length; i++)
			{
				center[i]=dpos[i+k*center.length];
			}
			centers.add(center);
		}
		//������Ӧ�Ⱥ�����ֵ��������ʵ���Ǿ��룬����ÿ�����ݵ�
		for (int i = 0; i <result.size(); i++){
			double  m_dFitnessk = Double.MAX_VALUE;
			double []distance = new double[centers.size()];		
			//��������ݵ㵽���о������ĵľ���
			for(int k=0;k<centers.size();k++) {
				//���㵱ǰ��result.get(i)��ÿ�����ĵľ���.�����������ӵ�ά��
				for (int j = 0; j < result.get(i).size(); j++){
					distance[k] += Math.pow(result.get(i).get(j)-centers.get(k)[j], 2) ;				
				}
			}
			//ѡ������ľ���
			for(int k=0;k<distance.length;k++) {
				if(m_dFitnessk>distance[k]) {
					m_dFitnessk=distance[k];
				}
			}
			//�����ӵ���Ӧ��
			m_dFitness += m_dFitnessk;
		}
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



