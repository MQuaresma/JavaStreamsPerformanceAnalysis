import java.util.List;
import java.util.function.Supplier;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.function.IntSupplier;
import java.util.Random;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.function.BiFunction;
import java.time.*;
import java.util.*;

public class Benchmarks{

//-------------------------------------------------------------------------------------------//
//                                           T1                                              //
//-------------------------------------------------------------------------------------------//

    public static void T1(List<TransCaixa> transactions){
        SimpleEntry<Double,Double> bench_results;
        double[] transactions_array = transactions
                                            .stream()
                                            .mapToDouble(TransCaixa::getValor)
                                            .toArray();

        Supplier<Double> array_for_supplier = 
            () -> {
                double sum = 0.f;
                for(int i = 0; i < transactions_array.length; i++)
                    sum += transactions_array[i];
                return sum;
            };

        Supplier<Double> array_forEach_supplier = 
            () -> {
                double sum = 0.f;
                for(Double value : transactions_array)
                    sum += value;
                return sum;
            };

        Supplier<Double> Dstream_seq_supplier =
            () -> {
                return transactions
                            .stream()
                            .mapToDouble(TransCaixa::getValor)
                            .sum();
            };
        
        Supplier<Double> Dstream_parallel_supplier =
            () -> {
                return transactions
                            .parallelStream()
                            .mapToDouble(TransCaixa::getValor)
                            .sum();
            };

        Supplier<Double> stream_seq_supplier =
            () -> {
                return transactions
                            .stream()
                            .map(TransCaixa::getValor)
                            .reduce(0.0, (ac, n) -> ac + n);
            };

        Supplier<Double> stream_parallel_supplier =
            () -> {
                return transactions
                            .parallelStream()
                            .map(TransCaixa::getValor)
                            .reduce(0.0, (ac, n) -> ac + n);
            };
        
        bench_results = testeBoxGen(array_forEach_supplier);
        System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(array_for_supplier);
        System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(Dstream_seq_supplier);
        System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(stream_seq_supplier);
        System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(Dstream_parallel_supplier);
        System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(stream_parallel_supplier);
        System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    }

//-------------------------------------------------------------------------------------------//
//                                           T2                                              //
//-------------------------------------------------------------------------------------------//

    public static void T2(List<TransCaixa> transactions){
        SimpleEntry<Double,Collection<TransCaixa>> bench_results;
        Comparator<TransCaixa> byDate = 
            (TransCaixa tc1, TransCaixa tc2) -> {
                LocalDateTime t1 = tc1.getData();
                LocalDateTime t2 = tc2.getData();
                if(t1.isBefore(t2))
                    return -1;
                else if(t1.equals(t2))
                    return 0;
                else return 1;  
            };
        
        Supplier<Set<TransCaixa>> sort_treeset = 
            () -> {
                TreeSet<TransCaixa> treesorted = new TreeSet<>(byDate);
                treesorted.addAll(transactions);
                return treesorted;   
            };

        Supplier<List<TransCaixa>> sort_inplace = 
            () -> {
                List<TransCaixa> sorted = new ArrayList<>();
                sorted.addAll(transactions);
                sorted.sort(byDate);
                return sorted;
            };

        Supplier<List<TransCaixa>> sort_seq_stream = 
            () -> {
                return transactions.stream().sorted(byDate).collect(Collectors.toList());
            };

        Supplier<List<TransCaixa>> sort_parallel_stream = 
            () -> {
                return transactions.parallelStream().sorted(byDate).collect(Collectors.toList());
            };

        bench_results = testeBoxGen(sort_treeset);
        System.out.println("Sorted in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(sort_inplace);
        System.out.println("Sorted in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(sort_seq_stream);
        System.out.println("Sorted in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(sort_parallel_stream);
        System.out.println("Sorted in " + bench_results.getKey() + "s");
    }

//-------------------------------------------------------------------------------------------//
//                                           T3                                              //
//-------------------------------------------------------------------------------------------//

    public static void T3(){
        SimpleEntry<Double,List<Integer>> bench_results_list;
        SimpleEntry<Double,int[]> bench_results_array;
        SimpleEntry<Double,IntStream> bench_results_stream;
        Random int_generator = new Random();

        int[] random_ints_array = int_generator.ints(1000000, 1, 9999).toArray();
        List<Integer> random_ints_list = Arrays.stream(random_ints_array).boxed().collect(Collectors.toList());;
        
        Supplier<List<Integer>> rd_list_stream = 
            () -> {
                return random_ints_list
                                    .stream()
                                    .distinct()
                                    .collect(Collectors.toList());
            };
        
        Supplier<List<Integer>> rd_list_parallel = 
            () -> {
                return random_ints_list
                                    .parallelStream()
                                    .distinct()
                                    .collect(Collectors.toList());
            };

        Supplier<int[]> rd_array_stream =
            () -> {
                return Arrays.stream(random_ints_array)
                             .distinct()
                             .toArray();
            };
        
        Supplier<int[]> rd_array_parallel =
            () -> {
                return Arrays.stream(random_ints_array)
                             .parallel()
                             .distinct()
                             .toArray();
            };
        

        Supplier<IntStream> rd_intstream_stream =
            () -> {
                return Arrays.stream(random_ints_array).distinct();
            };
         
        Supplier<IntStream> rd_intstream_parallel =
            () -> {
                return Arrays.stream(random_ints_array).parallel().distinct();
            };
        

        bench_results_list = testeBoxGen(rd_list_stream);
        System.out.println("Removed duplicated data in " + bench_results_list.getKey() + "s");
        bench_results_list = testeBoxGen(rd_list_parallel);
        System.out.println("Removed duplicated data in " + bench_results_list.getKey() + "s");
        bench_results_array = testeBoxGen(rd_array_stream);
        System.out.println("Removed duplicated data in " + bench_results_array.getKey() + "s");
        bench_results_array = testeBoxGen(rd_array_parallel);
        System.out.println("Removed duplicated data in " + bench_results_array.getKey() + "s");
        bench_results_stream = testeBoxGen(rd_intstream_stream);
        System.out.println("Removed duplicated data in " + bench_results_stream.getKey() + "s");
        bench_results_stream = testeBoxGen(rd_intstream_parallel);
        System.out.println("Removed duplicated data in " + bench_results_stream.getKey() + "s");
    }

//-------------------------------------------------------------------------------------------//
//                                           T4                                              //
//-------------------------------------------------------------------------------------------//

    private static double method_multiplication (double number_1, double number_2){
        return number_1 * number_2;
    }

    interface LambdaFunc{ 
        double operation(double a, double b); 
    }

    private double operate(double a, double b, LambdaFunc fobj){ 
        return fobj.operation(a, b); 
    } 

    public static void T4(List<TransCaixa> transactions){
        SimpleEntry<Double, double[]> bench_results;
        
        BiFunction<Double, Double, Double> bi_multiplication = (x, y) -> {      
            return x * y;
        };

        LambdaFunc lambda_multiplication = (double x, double y) -> x * y;

        double[] transactions_array = transactions
                                            .stream()
                                            .mapToDouble(TransCaixa::getValor)
                                            .toArray();

        Benchmarks b = new Benchmarks();

        Supplier<double[]> mult_method_stream =
        () -> {
            return Arrays.stream(transactions_array)
                         .map(t -> method_multiplication(t,t))
                         .toArray();
        };

        Supplier<double[]> mult_method_parallel =
        () -> {
            return Arrays.stream(transactions_array)
                         .parallel()
                         .map(t -> method_multiplication(t,t))
                         .toArray();
        };

        Supplier<double[]> mult_bi_stream =
        () -> {
            return Arrays.stream(transactions_array)
                         .map(t -> bi_multiplication.apply(t,t))
                         .toArray();
        };

        Supplier<double[]> mult_bi_parallel =
        () -> {
            return Arrays.stream(transactions_array)
                         .parallel()
                         .map(t -> bi_multiplication.apply(t,t))
                         .toArray();
        };

        Supplier<double[]> mult_lambda_stream =
        () -> {
            return Arrays.stream(transactions_array)
                         .map(t -> b.operate(t,t,lambda_multiplication))
                         .toArray();
        };

        Supplier<double[]> mult_lambda_parallel =
        () -> {
            return Arrays.stream(transactions_array)
                         .parallel()
                         .map(t -> b.operate(t,t,lambda_multiplication))
                         .toArray();
        };

        bench_results = testeBoxGen(mult_method_stream);
        System.out.println("Calculated in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(mult_method_parallel);
        System.out.println("Calculated in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(mult_bi_stream);
        System.out.println("Calculated in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(mult_bi_stream);
        System.out.println("Calculated in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(mult_lambda_stream);
        System.out.println("Calculated in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(mult_lambda_parallel);
        System.out.println("Calculated in " + bench_results.getKey() + "s");
    }

//-------------------------------------------------------------------------------------------//
//                                           T5                                              //
//-------------------------------------------------------------------------------------------//

public static void T5(List<TransCaixa> transactions){
    SimpleEntry<Double, TreeSet<TransCaixa>> bench_results_tree;
    SimpleEntry<Double, List<TransCaixa>> bench_results_list;

    Comparator<TransCaixa> byValor = 
        (TransCaixa tc1, TransCaixa tc2) -> {
            double t1 = tc1.getValor();
            double t2 = tc2.getValor();
            if(t1 > t2)
                return 1;
            else if(t1 == t2)
                return 0;
            else return -1;  
        };

    Supplier<TreeSet<TransCaixa>> sort_treeset_stream = 
        () -> {
            TreeSet<TransCaixa> treesorted = new TreeSet<>(byValor);
            transactions.stream()
                        .map(t -> treesorted.add(t));
            return treesorted;   
        };

    Supplier<TreeSet<TransCaixa>> sort_treeset_parallel = 
        () -> {
            TreeSet<TransCaixa> treesorted = new TreeSet<>(byValor);
            transactions.parallelStream()
                        .map(t -> treesorted.add(t));
            return treesorted;   
        };

    Supplier<List<TransCaixa>> sort_seq_stream = 
        () -> {
            List<TransCaixa> listsorted = new ArrayList<>();
            transactions.stream()
                        .sorted(byValor)
                        .map(t -> listsorted.add(t));
            return listsorted;
        };

    Supplier<List<TransCaixa>> sort_seq_parallel = 
        () -> {
            List<TransCaixa> listsorted = new ArrayList<>();
            transactions.parallelStream()
                        .sorted(byValor)
                        .map(t -> listsorted.add(t));
            return listsorted;
        };

    bench_results_tree = testeBoxGen(sort_treeset_stream);
    System.out.println("Sorted in " + bench_results_tree.getKey() + "s");
    bench_results_tree = testeBoxGen(sort_treeset_parallel);
    System.out.println("Sorted in " + bench_results_tree.getKey() + "s");
    bench_results_list = testeBoxGen(sort_seq_stream);
    System.out.println("Sorted in " + bench_results_list.getKey() + "s");
    bench_results_list = testeBoxGen(sort_seq_parallel);
    System.out.println("Sorted in " + bench_results_list.getKey() + "s");
}
    
//-------------------------------------------------------------------------------------------//
//                                           T7                                              //
//-------------------------------------------------------------------------------------------//

public static void T7(List<TransCaixa> transactions){
    SimpleEntry<Double, Double> bench_results;

    Spliterator<TransCaixa> spliterator1 = (new ArrayList<>(transactions)).spliterator();
    Spliterator<TransCaixa> spliterator2 = spliterator1.trySplit();
    Spliterator<TransCaixa> spliterator3 = spliterator2.trySplit();
    Spliterator<TransCaixa> spliterator4 = spliterator1.trySplit();

    Supplier<Double> list_sum_list = 
        () -> {
            double sum = 0.f;
            for (TransCaixa t : transactions)
                sum += t.getValor();
            return sum;
        };
    
    Supplier<Double> list_sum_stream = 
        () -> {
            return transactions.stream()
                               .mapToDouble(TransCaixa::getValor)
                               .sum();
        };
    
    Supplier<Double> list_sum_parallel = 
        () -> {
            return transactions.parallelStream()
                               .mapToDouble(TransCaixa::getValor)
                               .sum();
        };

    /*Supplier<Double> spliterator_sum_foreach =
        () -> {
            double sum = 0;
            spliterator1.forEachRemaining(t -> sum += t.getValor());
            return sum;
        };*/

    bench_results = testeBoxGen(list_sum_list);
    System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    bench_results = testeBoxGen(list_sum_stream);
    System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    bench_results = testeBoxGen(list_sum_parallel);
    System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    
}

//-------------------------------------------------------------------------------------------//
//                                           T8                                              //
//-------------------------------------------------------------------------------------------//


// 13 - 04 - 2017

public static void T8(List<TransCaixa> transactions){
    SimpleEntry<Double, String> bench_results;

    Supplier<String> java_7_biggest_tcode = 
    () -> {
        LocalDateTime ld1 = LocalDateTime.of(2017, 02, 20, 16, 0);
        LocalDateTime ld2 = LocalDateTime.of(2017, 02, 20, 22, 0);
        String r = "";
        double v = Double.MIN_NORMAL;
        LocalDateTime ldt;
        LocalTime lt;
        for (TransCaixa t : transactions){
            ldt = t.getData();
            lt = ldt.toLocalTime();
            if (t.getData().isAfter(ld1) && t.getData().isBefore(ld2)){
                if (t.getValor() > v){ r = t.getTrans(); v = t.getValor(); }
            }
        }
        return r;    
    };

    Comparator<TransCaixa> byValor = 
        (TransCaixa tc1, TransCaixa tc2) -> {
            double t1 = tc1.getValor();
            double t2 = tc2.getValor();
            if(t1 > t2)
                return 1;
            else if(t1 == t2)
                return 0;
            else return -1;  
        };

    Supplier<String> java_8_biggest_tcode = 
    () -> {
        LocalDateTime ld1 = LocalDateTime.of(2017, 02, 20, 16, 0);
        LocalDateTime ld2 = LocalDateTime.of(2017, 02, 20, 22, 0);
        double v = 0; String r = "";
        Optional<TransCaixa> tr = transactions.stream()  
                    .filter(t -> t.getData().isAfter(ld1) && t.getData().isBefore(ld2))
                    .max(byValor);
        return tr.get().getTrans();
    };

    Supplier<String> java_8_biggest_tcode_parallel = 
    () -> {
        LocalDateTime ld1 = LocalDateTime.of(2017, 02, 20, 16, 0);
        LocalDateTime ld2 = LocalDateTime.of(2017, 02, 20, 22, 0);
        double v = 0; String r = "";
        Optional<TransCaixa> tr = transactions.parallelStream()  
                    .filter(t -> t.getData().isAfter(ld1) && t.getData().isBefore(ld2))
                    .max(byValor);
        return tr.get().getTrans();
    };

    bench_results = testeBoxGen(java_7_biggest_tcode);
    System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    bench_results = testeBoxGen(java_8_biggest_tcode);
    System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    bench_results = testeBoxGen(java_8_biggest_tcode_parallel);
    System.out.println("Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");

}


//-------------------------------------------------------------------------------------------//
//                                     TESTE_BOX_GEN                                         //
//-------------------------------------------------------------------------------------------//

    public static <R> SimpleEntry<Double,R> testeBoxGen(Supplier<? extends R> supplier) {
        for(int i = 0; i < 10; i ++) supplier.get();    //warmup caches
        System.gc();                                    //request garbage collector
        Crono.start();
        R resultado = supplier.get();
        Double tempo = Crono.stop();
        return new SimpleEntry<Double,R>(tempo, resultado);
    }
}