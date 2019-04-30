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
			Agent.gbest = new double[Agent.iAgentDim*Agent.ikmeans]; 
			Agent.result.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println("花费时间"+(end-start)/1000+"秒");
	}
}


