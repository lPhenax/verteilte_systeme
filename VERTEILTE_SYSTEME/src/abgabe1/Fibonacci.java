package abgabe1;

public class Fibonacci {
	
	public static int StartFiboMitPara(final int n){
		
		if(n == 0) return 0;
		if(n == 1) return 1;
		return StartFiboMitPara(n-1) + StartFiboMitPara(n-2);
	}

//	public static void StartFibo(){
//		System.out.println("/****************************************************************/\n");
//		System.out.println("+++++ Es folgt die Fibonacci-Folge! +++++");
//		System.out.print(0 + " ");
//		System.out.print(1 + " ");
//		System.out.print(1 + " ");
//		System.out.print(2 + " ");
//		System.out.print(3 + " ");
//		System.out.print(5 + " ");
//		System.out.print(8 + " ");
//		System.out.print(13 + " ");
//		System.out.print(21 + " ");
//		System.out.print(34 + " ");
//		System.out.println("\n+++++ Das waren die ersten 10 Zahlen der Fibonacci-Folge +++++");
//		System.out.println("---> Formel = fn = fn-1 + fn-2");
//		System.out.println("\n/****************************************************************/");
//	}
	
	public static String StartFibo(){
		return "+++++ Es folgt die Fibonacci-Folge! +++++\n" + 
				"0 1 1 2 3 5 8 13 21 34 ---> Formel = fn = fn-1 + fn-2" +
				"\n+++++ Das waren die ersten 10 Zahlen der Fibonacci-Folge +++++\n"; 
				
	}
	
	public static void main(String[] args){
		System.out.println(StartFibo());
		for(int i = 0; i < 10; i++){
			System.out.println("Eingabeparameter = " + i + " und als Fibonaccizahl kommt '" + StartFiboMitPara(i) + "' raus.");
		}
		System.out.println("Eingabeparameter = " + 5 + " und als Fibonaccizahl kommt '" + StartFiboMitPara(5) + "' raus.");
	}
}
