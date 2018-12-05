package PSOVersion2;
import java.io.IOException;

class PSO
{
	private Agent[] agent;
	private final int iStep = 1000; //��������
	private double m_dBestFitness; //PSO�������ӵ�ȫ�ּ�ֵ
	private int m_iTempPos; //��¼����ȫ������ֵ��Ӧ���±�

	
	//���캯��,����readAgent�����������ݼ�����
	public PSO() {}

	public void readfile() throws IOException
	{
		Agent fristagent=new Agent();
		fristagent.readAgent();
	}

	//��������
	public void createpso()
	{
		//��������Ⱥ��ȫ�����ż�ֵ
		m_dBestFitness = Integer.MAX_VALUE;
		agent = new Agent[Agent.iPOSNum];
		//����iPosnum����������
		for(int i =0;i < Agent.iPOSNum;i++)
		{ 
			agent[i] = new Agent();
			agent[i].agentinitialize();
		}
	}

	public void Initialize()
	{
		//����ÿ�����ӵ���Ӧ�ȵ�ֵ
		for(int i = 0;i < Agent.iPOSNum;i++)
		{
			agent[i].m_dBestfitness = 10000; //��ÿ�����ӵ���Ӧ�ȳ�ʼΪ10000
			agent[i].UpdateFitness();
		}
	}

	public void Search()
	{
		int k = 0;
		while(k < iStep) //1000
		{
			m_iTempPos = 99; //��¼����ȫ������ֵ��Ӧ���±�
			//���������ӵľֲ���ֵ���бȽϣ��ҵ�ȫ�ּ�ֵ
			for(int i =0; i< Agent.iPOSNum;i++)
			{
				//��Ϊ���������Ǿ���ԽСԽ�ã��ҵ����ż�ֵ������¼��Ӧ���±�
				if(agent[i].m_dBestfitness < m_dBestFitness)
				{
					m_dBestFitness = agent[i].m_dBestfitness;
					m_iTempPos = i;
				}
			}
			//����ҵ���ȫ�ּ�ֵ�������ȫ�ּ�ֵ��ͨ��ǰ���ҵ����±�
			if(m_iTempPos != 99)
			{
				//Ⱥ�弫ֵ����
				for(int i =0;i < Agent.iAgentDim*Agent.ikmeans;i++)
				{
					try {
						Agent.gbest[i] = agent[m_iTempPos].dpbest[i];
					} catch (Exception e) {
//						System.out.println("Agent.gbest[i]:"+Agent.gbest.length);
//						System.out.println(m_iTempPos);
//						System.out.println(i);
						e.printStackTrace();
					}
				}
			}
			//�����������ӵ��ٶȺ�λ�ã�����һ�����Ӿ͸���һ��
			for(int i = 0; i < Agent.iPOSNum;i++)
			{
				agent[i].UpdatePos();
				//�����������ӵ���Ӧ��ֵ
				agent[i].UpdateFitness();
			}
			k++;
//			System.out.println("��������:"+k+"ȫ������ʹ�ö�ֵΪ:"+m_dBestFitness);
		}
		//ѭ������
//		System.out.println("After " + k + " steps " + "the best value is " + m_dBestFitness );
//		System.out.println("The best position is :");
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