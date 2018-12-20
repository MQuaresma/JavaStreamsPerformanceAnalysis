/**
 *
 * @author fmm 2017
 */

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalQueries;
import static java.time.temporal.TemporalQueries.chronology;
import static java.time.temporal.TemporalQueries.localDate;
import static java.time.temporal.TemporalQueries.localTime;
import static java.time.temporal.TemporalQueries.offset;
import static java.time.temporal.TemporalQueries.precision;
import static java.time.temporal.TemporalQueries.zone; 
import static java.time.temporal.TemporalQueries.zoneId; 
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import java.time.*;
import static java.lang.System.out;
import java.util.Random;
import java.util.stream.*;
import java.util.stream.IntStream;
import java.util.Collections;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.counting;
import static java.util.Comparator.comparing;
import java.util.*;
import java.util.DoubleSummaryStatistics;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.lang.NumberFormatException;
import java.lang.System.*;
import static java.lang.System.out;
import java.nio.charset.StandardCharsets;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Callable;
import java.lang.Runnable;
import static java.util.concurrent.ForkJoinPool.commonPool;
import java.lang.Thread;
import java.util.Random;


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
    
    public static <R> SimpleEntry<Double,R> testeBoxGen(Supplier<? extends R> supplier) {
        Crono.start();
        R resultado = supplier.get();
        Double tempo = Crono.stop();
        return new SimpleEntry<Double,R>(tempo, resultado);
    }
    
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        
        String nomeFich = "transCaixa1M.txt";
        List<TransCaixa> ltc1 = new ArrayList<>();
        
        // LE O FICHEIRO DE TRANSACÇOES PARA List<TransCaixa> sem Streams 
        Crono.start();
        ltc1 = setup1(nomeFich);
        out.println("Setup com List<String>: " + Crono.stop()*1000 + " ms");
        out.println("Transacções lidas -  Listas: " + ltc1.size());
        ltc1.clear();
       
        // LE O FICHEIRO DE TRANSACÇOES PARA List<TransCaixa> com Streams
        Crono.start();
        ltc1 = setup(nomeFich);
        out.println("Setup com Streams: " + Crono.stop()*1000 + " ms");
        out.println("Transacções lidas - Streams: " + ltc1.size());
        //memoryUsage();
        
        final List<TransCaixa> ltc = new ArrayList<>(ltc1);
        
        DoubleSummaryStatistics stats = 
                                        ltc.stream().mapToDouble(TransCaixa::getValor).summaryStatistics();
        out.println("Stats: " + stats);
     }
}