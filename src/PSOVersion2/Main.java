package PSOVersion2;
import java.io.IOException;


public class Main 
{
	public static void main(String[] args) throws IOException 
	{
		for(int i=0;i<30;i++) {
			PSO pso = new PSO();
			pso.readfile();
			pso.createpso();
			pso.Initialize();
			pso.Search();			
		}
	}
}


