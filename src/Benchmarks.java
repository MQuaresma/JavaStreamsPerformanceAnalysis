import java.util.List;
import java.util.function.Supplier;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.function.IntSupplier;
import java.util.Random;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Benchmarks{
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

    public static void T3(){
        SimpleEntry<Double,List<Integer>> bench_results_list;
        SimpleEntry<Double,int[]> bench_results_array;
        Random int_generator = new Random();
        int[] random_ints_array = int_generator.ints(1000000, 1, 9999).toArray();
        IntStream random_ints_stream = Arrays.stream(random_ints_array);
        List<Integer> random_ints_list = Arrays.stream(random_ints_array).boxed().collect(Collectors.toList());
        
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

        bench_results_list = testeBoxGen(rd_list_stream);
        System.out.println("Removed duplicated data in " + bench_results_list.getKey() + "s");
        bench_results_list = testeBoxGen(rd_list_parallel);
        System.out.println("Removed duplicated data in " + bench_results_list.getKey() + "s");
        bench_results_array = testeBoxGen(rd_array_stream);
        System.out.println("Removed duplicated data in " + bench_results_array.getKey() + "s");

        
    }

    public static <R> SimpleEntry<Double,R> testeBoxGen(Supplier<? extends R> supplier) {
        for(int i = 0; i < 10; i ++) supplier.get();    //warmup caches
        System.gc();                                    //request garbage collector
        Crono.start();
        R resultado = supplier.get();
        Double tempo = Crono.stop();
        return new SimpleEntry<Double,R>(tempo, resultado);
    }
}