/**
 *
 * @author fmm 2017
 */

import java.time.*;
import static java.lang.System.out;
import java.util.stream.*;
import static java.util.stream.Collectors.toList;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.lang.NumberFormatException;
import java.nio.charset.StandardCharsets;


public class Trans_Caixa_Streams {
    
    public static void memoryUsage() {
		final int mByte = 1024*1024;
		// Parâmetros de RunTime
		Runtime runtime = Runtime.getRuntime();
		out.println("== Valores de Utilização da HEAP [MB] ==");
		out.println("Memória Máxima RT:" + runtime.maxMemory()/mByte);
		out.println("Total Memory:" + runtime.totalMemory()/mByte);
		out.println("Memória Livre:" + runtime.freeMemory()/mByte);
		out.println("Memoria Usada:" + (runtime.totalMemory() - 
                                                                                        runtime.freeMemory())/mByte);	
    }
    
    public static TransCaixa strToTransCaixa(String linha) {
       //
       double preco = 0.0; 
       int ano = 0; int mes = 0; int dia = 0; 
       int hora = 0; int min = 0; int seg = 0;
       String codTrans, codCaixa;
       // split()
       String[] campos = linha.split("/");
       codTrans = campos[0].trim();
       codCaixa = campos[1].trim();
       try {
             preco = Double.parseDouble(campos[2]); 
       }
       catch(InputMismatchException | NumberFormatException e) { return null; }        
       String[] diaMesAnoHMS = campos[3].split("T");
       String[] diaMesAno = diaMesAnoHMS[0].split(":");
       String[] horasMin = diaMesAnoHMS[1].split(":");
       try {
             dia = Integer.parseInt(diaMesAno[0]);
             mes = Integer.parseInt(diaMesAno[1]);
             ano = Integer.parseInt(diaMesAno[2]);
             hora = Integer.parseInt(horasMin[0]);
             min = Integer.parseInt(horasMin[1]);
       }
       catch(InputMismatchException | NumberFormatException e) { return null; }
       return TransCaixa.of(codTrans, codCaixa, preco, LocalDateTime.of(ano, mes, dia, hora, min, 0));    
    }
   
    public static List<TransCaixa> setup(String nomeFich) {
      List<TransCaixa> ltc = new ArrayList<>();
      try (Stream<String> sTrans = Files.lines(Paths.get(nomeFich))) {
               ltc = sTrans.map(linha -> strToTransCaixa(linha)).collect(toList());
      } 
      catch(IOException exc) { out.println(exc.getMessage()); } 
      return ltc;
    }
    
    public static List<TransCaixa> setup1(String nomeFich) {
      List<String> lines = new ArrayList<>();
      try { lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8); }
      catch(IOException exc) { System.out.println(exc.getMessage()); }
      // List<String> -> List<TransCaixa>
      List<TransCaixa> lTrans = new ArrayList<>();
      lines.forEach(line -> lTrans.add(strToTransCaixa(line)));
      return lTrans;
    }
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //REMOVE WHEN COMMITING
        String path_prefix = "";
        List<String> test_files = Arrays.asList("transCaixa1M.txt","transCaixa2M.txt","transCaixa4M.txt","transCaixa6M.txt");
        List<TransCaixa> transaction_list = new ArrayList<>();

        for(String file : test_files){
            transaction_list = setup(path_prefix+file);
            System.out.println("\n\n" + file + "\n\n");
            System.out.println("\nT1\n");
            Benchmarks.T1(transaction_list, true);
            System.out.println("\nT2\n");
            Benchmarks.T2(transaction_list, true);
            System.out.println("\nT3\n");
            Benchmarks.T3(transaction_list.size(), true);
            System.out.println("\nT4\n");
            Benchmarks.T4(transaction_list, true);
            System.out.println("\nT5\n");
            Benchmarks.T5(transaction_list, true);
            System.out.println("\nT6\n");
            Benchmarks.T6(transaction_list, true);
            System.out.println("\nT7\n");
            Benchmarks.T7(transaction_list, true);
            System.out.println("\nT8\n");
            Benchmarks.T8(transaction_list, true);
            System.out.println("\nT9\n");
            Benchmarks.T9(transaction_list, true);
            System.out.println("\nT10\n");
            Benchmarks.T10(transaction_list, true);
            System.out.println("\nT11\n");
            Benchmarks.T11(transaction_list, true);
            System.out.println("\nT12\n");
            Benchmarks.T12(transaction_list, true);
            System.gc();
        }
    }
}
