package abgabe2;

public class Fibonacci3 {
	
	public static int StartFiboMitPara(final int n){
		
		if(n == 0) return 0;
		if(n == 1) return 1;
		return StartFiboMitPara(n-1) + StartFiboMitPara(n-2);
	}

}
