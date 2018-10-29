package PSOVersion2;
import java.io.IOException;

class PSO
{
	private Agent[] agent;
	private final int iStep = 10000; //��������
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
		m_dBestFitness = 10000;
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
					Agent.gbest[i] = agent[m_iTempPos].dpbest[i];
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
		}
		//ѭ������
		System.out.println("After " + k + " steps " + "the best value is " + m_dBestFitness );
//		System.out.println("The best position is :");
		for(int i = 0;i < Agent.iAgentDim*Agent.ikmeans;i++)
		{
			System.out.print(Agent.gbest[i] + " ");
		}
		System.out.println();
	}

}