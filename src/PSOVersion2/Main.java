package PSOVersion2;
import java.io.IOException;



public class Main 
{
	public static void main(String[] args) throws IOException 
	{
		long start = System.currentTimeMillis(); 
		for(int i=0;i<30;i++) {
			PSO pso = new PSO();
			pso.readfile();
			pso.createpso();
			pso.Initialize();
			pso.Search();	
			//ÿ������һ�������̬���鶼Ҫ���¸�ֵ��
			Agent.gbest = new double[Agent.iAgentDim*Agent.ikmeans]; 
			Agent.result.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println("����ʱ�䣺"+(end-start)/1000+"��");
	}
}


