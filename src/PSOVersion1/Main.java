package PSOVersion1;
import java.io.IOException;



public class Main 
{
	public static void main(String[] args) throws IOException 
	{
		long start = System.currentTimeMillis();
		for(int i=0;i<10;i++) {
			PSO pso = new PSO();
			pso.readfile();
			pso.createpso();
			pso.Initialize();
			pso.Search();
			//每次跑完一遍这个静态数组都要重新赋值的
			Agent.gbest = new double[Agent.iAgentDim*Agent.ikmeans]; 
			Agent.result.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println("运行时间："+(end-start)/1000+"秒");
	}
}


