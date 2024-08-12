package test;

//IMPORTAMOS LIBRERIAS
import java.util.Random;
import java.util.concurrent.Semaphore;


public class Hilo extends Thread {

	//DEFINICION DE VARIABLES
	private static int columnas = 24;
	private static int renglones = 12;
	private static int [] [] matriz = new int [columnas] [renglones];
	
	//LA CANTIDAD DE HILOS = CANTIDAD DE NUCLEOS LOGICOS
	private static int cantidadHilos = Runtime.getRuntime().availableProcessors();
	private static Hilo [] arrayHilos = new Hilo [cantidadHilos];
	
	//ATRIBUTOS INTERNOS DEL HILO
	//---------------------------------------------------------
	private int idHilo;
	private int limiteInicio;
	private int limiteFinal;
	private int columnasAsignadas;
	private int acumuladorPropio;
	//---------------------------------------------------------
	
	//CONSTRUCTOR DEL HILO
	public Hilo(int idHilo, int limiteInicio, int limiteFinal, int columnasAsignadas) {
		super();
		this.idHilo = idHilo;
		this.limiteInicio = limiteInicio;
		this.limiteFinal = limiteFinal;
		this.columnasAsignadas = columnasAsignadas;
		this.acumuladorPropio = 0;
	}

	
	//RUN!!!
	//---------------------------------------------------------
	public void run () {
	
		System.out.println("Hola! Soy el Hilo " + this.idHilo + ". Mis limites son: [" + this.limiteInicio + "/" + this.limiteFinal + 
		"] y mis columnas asignadas son: " + this.columnasAsignadas + "\n");
		
		for (int c = this.limiteInicio; c < this.limiteFinal; c ++) {
			
			for (int r = 0; r < renglones; r ++) {
				
				if (matriz [c] [r] % 2 == 0) {
					
					this.acumuladorPropio ++;
				}
			}
		}
	}
	//---------------------------------------------------------

	
	//METODOS SECUENCIALES
	//---------------------------------------------------------
	public static void rellenarMatrizConAleatorios () {
		
		Random random = new Random ();
		
		for (int c = 0; c < columnas; c ++) {
			
			for (int r = 0; r < renglones; r ++) {
				
				matriz [c] [r] = random.nextInt(20) + 1;
			}
		}
	}
	
	public static void mostrarMatriz () {
		
		for (int c = 0; c < columnas; c ++) {
			
			for (int r = 0; r < renglones; r ++) {
				
				System.out.print("[" + matriz [c] [r] + "]\t");
			}
			
			System.out.println();
		}
	}
	
	public static int contarParesSecuencial () {
		
		int acumuladorPares = 0;
		
		for (int c = 0; c < columnas; c ++) {
			
			for (int r = 0; r < renglones; r ++) {
				
				if (matriz [c] [r] != 0 && matriz [c] [r] % 2 == 0) {
					
					acumuladorPares ++;
				}
			}
		}
		
		return acumuladorPares;
	}
	//---------------------------------------------------------
	
	//METODO CONCURRRENTE
	//---------------------------------------------------------
	public static int contarParesConcurrrente () {
		
		//EVALUAMOS SI LA CANTIDAD DE HILOS ES MAYOR A LA CANTIDAD DE COLUMNAS.
		//DE ASI SERLO, DEBEMOS DECREMENTAR LA CANTIDAD DE HILOS PARA NO GENERAR 
		//HILOS DE MAS.
		while (cantidadHilos > columnas) {
			
			cantidadHilos --;
		}
		
		//REPARTIMOS LOS HILOS POR COLUMNAS. CALCULAMOS LA CANTIDAD DE COLUMNAS QUE TENDRÁ ASIGNADA
		//CADA HILO.
		int columnasPorHilo = (int) columnas / cantidadHilos;
		
		//EN CASO DE QUE LA DIVISION NO DE EXACTA, DEBEMOS CALCULAR EL RESTO PARA REPARTIR ESA CANTIDAD QUE SOBRA
		//DE MANERA EQUITATIVA ENTRE LOS HILOS
		int columnasSinAsignar = columnas % cantidadHilos;
		
		//ASIGNAMOS LOS LIMITES A LOS HILOS. CARGAMOS EL ARRAY DE HILOS.
		int inicio = 0;
		int fin = 0;
		int columnasAsignadas = 0;
		
		for (int i = 0; i < cantidadHilos; i ++) {
			
			if (i < columnasSinAsignar) {
				
				fin = fin + columnasPorHilo + 1;
			
			} else {
				
				fin = fin + columnasPorHilo;
			}
			
			columnasAsignadas = fin - inicio;
			
			arrayHilos [i] = new Hilo (i, inicio, fin, columnasAsignadas);
			
			inicio = fin;
		}
		
		//LANZAMOS LOS HILOS.
		for (int i = 0; i < cantidadHilos; i ++) {
			
			arrayHilos [i].start();
		}
		
		//LOS ESPERAMOS.
		for (int i = 0; i < cantidadHilos; i ++) {
			
			try {
				arrayHilos [i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//UNIFICAMOS LOS ACUMULADORES PERSONALES DE CADA HILO EN UN ACUMULADOR GENERAL.
		int acumuladorPares = 0;
		
		for (int i = 0; i < cantidadHilos; i ++) {
			
			acumuladorPares = acumuladorPares + arrayHilos [i].acumuladorPropio;
		}
		
		return acumuladorPares;
	}
	//---------------------------------------------------------


	//MAIN
	public static void main (String args []) {
		
		//RELLENAMOS MATRIZ
		Hilo.rellenarMatrizConAleatorios();
		
		//LA MOSTRAMOS
		//Hilo.mostrarMatriz();
		
		double tiempoInicial;
		
		//SECUENCIAL
		//---------------------------------------------------------
		tiempoInicial = System.nanoTime();
		
		System.out.println("\nLa cantidad de pares contados de manera SECUENCIAL fue de: " + Hilo.contarParesSecuencial() 
		+ " y tardó un total de: " + (System.nanoTime() - tiempoInicial) / 1000 + " microSeg\n");
		//---------------------------------------------------------
		
		//CONCURRENTE
		//---------------------------------------------------------
		tiempoInicial = System.nanoTime();
		
		int cantidadPares;
		
		cantidadPares = Hilo.contarParesConcurrrente();
		
		System.out.println("\nLa cantidad de pares contados de manera CONCURRENTE fue de: " + cantidadPares +  
		" y tardó un total de: " + (System.nanoTime() - tiempoInicial) / 1000 + " microSeg\n");
		//---------------------------------------------------------		
	}
}