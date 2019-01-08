import java.time.temporal.ChronoField;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
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
import java.util.function.Function;
import java.util.function.BiFunction;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;
import static java.util.stream.Collectors.*;

public class Benchmarks{

//-------------------------------------------------------------------------------------------//
//                                           T1                                              //
// Calculate sum of transaction values                                                       //
//-------------------------------------------------------------------------------------------//

    public static void T1(List<TransCaixa> transactions, boolean sampling){
        SimpleEntry<Double,Double> bench_results;
        double[] transactions_array = transactions
                                            .stream()
                                            .mapToDouble(TransCaixa::getValor)
                                            .toArray();

        Supplier<Double> array_for_supplier_sum= 
            () -> {
                double sum = 0.f;
                for(int i = 0; i < transactions_array.length; i++)
                    sum += transactions_array[i];
                return sum;
            };

        Supplier<Double> array_forEach_supplier_sum= 
            () -> {
                double sum = 0.f;
                for(Double value : transactions_array)
                    sum += value;
                return sum;
            };

        Supplier<Double> Dstream_seq_supplier_sum=
            () -> {
                return transactions
                            .stream()
                            .mapToDouble(TransCaixa::getValor)
                            .sum();
            };
        
        Supplier<Double> Dstream_parallel_supplier_sum=
            () -> {
                return transactions
                            .parallelStream()
                            .mapToDouble(TransCaixa::getValor)
                            .sum();
            };

        Supplier<Double> stream_seq_supplier_sum=
            () -> {
                return transactions
                            .stream()
                            .map(TransCaixa::getValor)
                            .reduce(0.0, (ac, n) -> ac + n);
            };

        Supplier<Double> stream_parallel_supplier_sum=
            () -> {
                return transactions
                            .parallelStream()
                            .map(TransCaixa::getValor)
                            .reduce(0.0, (ac, n) -> ac + n);
            };
        
            Supplier<Double> array_for_supplier_avg= 
            () -> {
                double sum = 0.f;
                for(int i = 0; i < transactions_array.length; i++)
                    sum += transactions_array[i];
                return sum/transactions_array.length;
            };

        Supplier<Double> array_forEach_supplier_avg= 
            () -> {
                double sum = 0.f;
                for(Double value : transactions_array)
                    sum += value;
                return sum/transactions_array.length;
            };

        Supplier<Double> Dstream_seq_supplier_avg=
            () -> {
                return transactions
                            .stream()
                            .mapToDouble(TransCaixa::getValor)
                            .average().getAsDouble();
            };
        
        Supplier<Double> Dstream_parallel_supplier_avg=
            () -> {
                return transactions
                            .parallelStream()
                            .mapToDouble(TransCaixa::getValor)
                            .average().getAsDouble();
            };

        Supplier<Double> stream_seq_supplier_avg=
            () -> {
                return transactions
                            .stream()
                            .map(TransCaixa::getValor)
                            .collect(Collectors.averagingDouble(Double::new));
            };

        Supplier<Double> stream_parallel_supplier_avg=
            () -> {
                return transactions
                            .parallelStream()
                            .map(TransCaixa::getValor)
                            .collect(Collectors.averagingDouble(Double::new));
            };
        
        if(!sampling){
            bench_results = testeBoxGen(array_forEach_supplier_sum);
            System.out.println("[Array:forEach]           Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(array_for_supplier_sum);
            System.out.println("[Array:for]               Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(Dstream_seq_supplier_sum);
            System.out.println("[DoubleStream:Sequential] Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(stream_seq_supplier_sum);
            System.out.println("[Stream:Sequential]       Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(Dstream_parallel_supplier_sum);
            System.out.println("[DoubleStream:Parallel]   Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(stream_parallel_supplier_sum);
            System.out.println("[Stream:Parallel]         Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        }else{
            /*
            System.out.println("[Array:forEach]           Computed  in " + sampler(array_forEach_supplier_sum) + "s");
            System.out.println("[Array:for]               Computed  in " + sampler(array_for_supplier_sum) + "s");
            System.out.println("[DoubleStream:Sequential] Computed  in " + sampler(Dstream_seq_supplier_sum) + "s");
            System.out.println("[Stream:Sequential]       Computed  in " + sampler(stream_seq_supplier_sum) + "s");
            System.out.println("[DoubleStream:Parallel]   Computed  in " + sampler(Dstream_parallel_supplier_sum) + "s");
            System.out.println("[Stream:Parallel]         Computed  in " + sampler(stream_parallel_supplier_sum) + "s");
            */
            System.out.println("[Array:forEach]           Computed  in " + sampler(array_forEach_supplier_avg) + "s");
            System.out.println("[Array:for]               Computed  in " + sampler(array_for_supplier_avg) + "s");
            System.out.println("[DoubleStream:Sequential] Computed  in " + sampler(Dstream_seq_supplier_avg) + "s");
            System.out.println("[Stream:Sequential]       Computed  in " + sampler(stream_seq_supplier_avg) + "s");
            System.out.println("[DoubleStream:Parallel]   Computed  in " + sampler(Dstream_parallel_supplier_avg) + "s");
            System.out.println("[Stream:Parallel]         Computed  in " + sampler(stream_parallel_supplier_avg) + "s");
        }
    }

//-------------------------------------------------------------------------------------------//
//                                           T2                                              //
// Fetch portion of Collection based on sorting criteria (first/last 30%)                    //
//-------------------------------------------------------------------------------------------//

    public static void T2(List<TransCaixa> transactions, boolean sampling){
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
        
        Supplier<List<TransCaixa>> sort_treeset_seq_stream =
            () -> {
                TreeSet<TransCaixa> transactions_tree = new TreeSet<>(byDate);
                transactions_tree.addAll(transactions);
                List<TransCaixa> first_30 = transactions_tree.stream()
                                                             .limit((long) (transactions_tree.size() * 0.3))
                                                             .collect(Collectors.toList());
                List<TransCaixa> last_30 = transactions_tree.stream()
                                                            .skip((long) (transactions_tree.size() * 0.7))
                                                            .collect(Collectors.toList());
                return new ArrayList<TransCaixa>() {{addAll(first_30); addAll(last_30);}};
            };

        Supplier<List<TransCaixa>> sort_treeset_parallel_stream =
            () -> {
                TreeSet<TransCaixa> transactions_tree = new TreeSet<>(byDate);
                transactions_tree.addAll(transactions);
                List<TransCaixa> first_30 = transactions_tree.parallelStream()
                                                             .limit((long) (transactions_tree.size() * 0.3))
                                             .collect(Collectors.toList());
                List<TransCaixa> last_30 = transactions_tree.parallelStream()
                                                            .skip((long) (transactions_tree.size() * 0.7))
                                                            .collect(Collectors.toList());
                return new ArrayList<TransCaixa>() {{addAll(first_30); addAll(last_30);}};
            };
        
        Supplier<List<TransCaixa>> sort_list_seq_stream =
            () -> {
                List<TransCaixa> transactions_list = new ArrayList<>();
                transactions_list.addAll(transactions);
                List<TransCaixa> first_30 = transactions_list.stream()
                                                             .sorted(byDate)
                                                             .limit((long) (transactions_list.size() * 0.3))
                                                             .collect(Collectors.toList());
                List<TransCaixa> last_30 = transactions_list.stream()
                                                            .sorted(byDate)
                                                            .skip((long) (transactions_list.size() * 0.7))
                                                            .collect(Collectors.toList());
                return new ArrayList<TransCaixa>() {{addAll(first_30); addAll(last_30);}};
            };

        Supplier<List<TransCaixa>> sort_list_parallel_stream =
            () -> {
                List<TransCaixa> transactions_list = new ArrayList<>();
                transactions_list.addAll(transactions);
                List<TransCaixa> first_30 = transactions_list.parallelStream()
                                                             .sorted(byDate)
                                                             .limit((long) (transactions_list.size() * 0.3))
                                                             .collect(Collectors.toList());
                List<TransCaixa> last_30 = transactions_list.parallelStream()
                                                            .sorted(byDate)
                                                            .skip((long) (transactions_list.size() * 0.7))
                                                            .collect(Collectors.toList());
                return new ArrayList<TransCaixa>() {{addAll(first_30); addAll(last_30);}};
            };
        
        if(!sampling){
            bench_results = testeBoxGen(sort_treeset_seq_stream);
            System.out.println("[TreeSet - Sequential Stream] Sorted in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(sort_treeset_parallel_stream);
            System.out.println("[TreeSet - Parallel Stream]   Sorted in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(sort_list_seq_stream);
            System.out.println("[List - Sequential Stream]    Sorted in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(sort_list_parallel_stream);
            System.out.println("[List - Parallel Stream]      Sorted in " + bench_results.getKey() + "s");
        }else{
            System.out.println("[TreeSet - Sequential Stream] Sorted in " + sampler(sort_treeset_seq_stream) + "s");
            System.out.println("[TreeSet - Parallel Stream]   Sorted in " + sampler(sort_treeset_parallel_stream) + "s");
            System.out.println("[List - Sequential Stream]    Sorted in " + sampler(sort_list_seq_stream) + "s");
            System.out.println("[List - Parallel Stream]      Sorted in " + sampler(sort_list_parallel_stream) + "s");
        }
    }

//-------------------------------------------------------------------------------------------//
//                                           T3                                              //
//  Remove duplicates from Collection                                                        //
//  length: no. of elements in Collection                                                    //
//-------------------------------------------------------------------------------------------//

    public static void T3(int length, boolean sampling){
        SimpleEntry<Double,List<Integer>> bench_results_list;
        SimpleEntry<Double,int[]> bench_results_array;
        Random int_generator = new Random();

        int[] random_ints_array = int_generator.ints(length, 1, 9999).toArray();
        List<Integer> random_ints_list = Arrays.stream(random_ints_array).boxed().collect(Collectors.toList());;
        
        Supplier<List<Integer>> rd_list = 
            () -> {
                List<Integer> result = new ArrayList<>();
                random_ints_list.forEach(i -> {
                    if(!result.contains(i)) result.add(i);
                });
                return result;
            };

        Supplier<int[]> rd_array =
            () -> {
                int[] result = new int[random_ints_array.length];
                int non_dups=0;
                boolean dup;

                for(int elem : result){
                    dup = false;
                    for(int j = 0; !dup && j < non_dups; j++)
                        dup = result[j] == elem;
                    if(!dup) result[non_dups++]=elem;
                }
                return result;
            };

        Supplier<List<Integer>> rd_intstream_stream =
            () -> {
                return Arrays.stream(random_ints_array).distinct().boxed().collect(Collectors.toList());
            };

        if(!sampling){
            bench_results_list = testeBoxGen(rd_list);
            System.out.println("[List]      Removed duplicated data in " + bench_results_list.getKey() + "s");
            bench_results_array = testeBoxGen(rd_array);
            System.out.println("[Array]     Removed duplicated data in " + bench_results_array.getKey() + "s");
            bench_results_list = testeBoxGen(rd_intstream_stream);
            System.out.println("[IntStream] Removed duplicated data in " + bench_results_list.getKey() + "s");
        }else{
            System.out.println("[List]      Removed duplicated data in " + sampler(rd_list) + "s");
            System.out.println("[Array]     Removed duplicated data in " + sampler(rd_array) + "s");
            System.out.println("[IntStream] Removed duplicated data in " + sampler(rd_intstream_stream) + "s");
        }
    }

//-------------------------------------------------------------------------------------------//
//                                           T4                                              //
// Product of double values                                                                  //
//-------------------------------------------------------------------------------------------//

    private static double method_multiplication (double number_1, double number_2){
        return number_1 * number_2;
    }

    public static void T4(List<TransCaixa> transactions, boolean sampling){
        SimpleEntry<Double, double[]> bench_results;
        
        BiFunction<Double, Double, Double> bi_multiplication = (x, y) -> {      
            return x * y;
        };

        Function<Double, Function<Double,Double>> lambda_mul = x -> y -> x * y;

        double[] transactions_array = transactions
                                            .stream()
                                            .mapToDouble(TransCaixa::getValor)
                                            .toArray();

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
                         .map(t -> lambda_mul.apply(t).apply(t))
                         .toArray();
        };

        Supplier<double[]> mult_lambda_parallel =
        () -> {
            return Arrays.stream(transactions_array)
                         .parallel()
                         .map(t -> lambda_mul.apply(t).apply(t))
                         .toArray();
        };

        if(!sampling){
            bench_results = testeBoxGen(mult_method_stream);
            System.out.println("[Static Method : Stream: Sequential] Calculated in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(mult_method_parallel);
            System.out.println("[Static Method : Stream: Parallel]   Calculated in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(mult_bi_stream);
            System.out.println("[BiFunction : Stream: Sequential]    Calculated in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(mult_bi_parallel);
            System.out.println("[BiFunction : Stream: Parallel]      Calculated in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(mult_lambda_stream);
            System.out.println("[Lambda : Stream: Sequential]        Calculated in " + bench_results.getKey() + "s");
            bench_results = testeBoxGen(mult_lambda_parallel);
            System.out.println("[Lambda : Stream: Parallel]          Calculated in " + bench_results.getKey() + "s");
        }else{
            System.out.println("[Static Method : Stream: Sequential] Calculated in " + sampler(mult_method_stream) + "s");
            System.out.println("[Static Method : Stream: Parallel]   Calculated in " + sampler(mult_method_parallel) + "s");
            System.out.println("[BiFunction : Stream: Sequential]    Calculated in " + sampler(mult_bi_stream) + "s");
            System.out.println("[BiFunction : Stream: Parallel]      Calculated in " + sampler(mult_bi_parallel) + "s");
            System.out.println("[Lambda : Stream: Sequential]        Calculated in " + sampler(mult_lambda_stream) + "s");
            System.out.println("[Lambda : Stream: Parallel]          Calculated in " + sampler(mult_lambda_parallel) + "s");
        }
    }

//-------------------------------------------------------------------------------------------//
//                                           T5                                              //
// Sort according to Comparator                                                              //
//-------------------------------------------------------------------------------------------//

public static void T5(List<TransCaixa> transactions, boolean sampling){
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
            return transactions.stream()
                               .collect(Collectors.toCollection(() -> new TreeSet<>(byValor)));
        };

    Supplier<TreeSet<TransCaixa>> sort_treeset_parallel = 
        () -> {
            return transactions.parallelStream()
                               .collect(Collectors.toCollection(() -> new TreeSet<>(byValor)));
        };

    Supplier<List<TransCaixa>> sort_seq_stream = 
        () -> {
            return transactions.stream()
                                .sorted(byValor)
                                .collect(Collectors.toList());;
        };

    Supplier<List<TransCaixa>> sort_seq_parallel = 
        () -> {
            return transactions.parallelStream()
                        .sorted(byValor)
                        .collect(Collectors.toList());
        };

    if(!sampling){
        bench_results_tree = testeBoxGen(sort_treeset_stream);
        System.out.println("[TreeSet: Stream : Sequential] Sorted in " + bench_results_tree.getKey() + "s");
        bench_results_tree = testeBoxGen(sort_treeset_parallel);
        System.out.println("[TreeSet: Stream : Parallel]   Sorted in " + bench_results_tree.getKey() + "s");
        bench_results_list = testeBoxGen(sort_seq_stream);
        System.out.println("[List: Stream : Sequential]    Sorted in " + bench_results_list.getKey() + "s");
        bench_results_list = testeBoxGen(sort_seq_parallel);
        System.out.println("[List: Stream : Parallel]      Sorted in " + bench_results_list.getKey() + "s");
    }else{
        System.out.println("[TreeSet: Stream : Sequential] Sorted in " + sampler(sort_treeset_stream) + "s");
        System.out.println("[TreeSet: Stream : Parallel]   Sorted in " + sampler(sort_treeset_parallel) + "s");
        System.out.println("[List: Stream : Sequential]    Sorted in " + sampler(sort_seq_stream) + "s");
        System.out.println("[List: Stream : Parallel]      Sorted in " + sampler(sort_seq_parallel) + "s");
    }
}

//-------------------------------------------------------------------------------------------//
//                                           T6                                              //
// Group values according to month, day, hour                                                //
//-------------------------------------------------------------------------------------------//

public static void T6(List<TransCaixa> transactions, boolean sampling){
    SimpleEntry<Double, Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>>> bench_results;

    Supplier<Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>>> stream_grouper = 
        () -> {
            return transactions.stream()
                        .collect(groupingBy(t -> t.getData().getMonthValue(),
                                            groupingBy(t -> t.getData().getDayOfMonth(),
                                                        groupingBy(t -> t.getData().getHour()))));
        };

    Supplier<Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>>> parallel_stream_grouper = 
        () -> {
            return transactions.parallelStream()
                        .collect(groupingBy(t -> t.getData().getMonthValue(),
                                            groupingBy(t -> t.getData().getDayOfMonth(),
                                                        groupingBy(t -> t.getData().getHour()))));
        };    
    
    Supplier<Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>>> iterator_grouper = 
        () -> {
            Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>> res = new HashMap<>();;
            Map<Integer, Map<Integer, List<TransCaixa>>> day_hour;
            Map<Integer, List<TransCaixa>> hour_transactions;
            List<TransCaixa> list;
            TransCaixa aux;
            LocalDateTime aux_dt;
            Iterator<TransCaixa> it = transactions.iterator();

            while(it.hasNext()){
                aux = it.next();
                aux_dt = aux.getData();
                day_hour = res.getOrDefault(aux_dt.getMonthValue(), new HashMap<>());
                hour_transactions = day_hour.getOrDefault(aux_dt.getDayOfMonth(), new HashMap<>());  
                list = hour_transactions.getOrDefault(aux_dt.getHour(), new ArrayList<>());
                list.add(aux);
                hour_transactions.put(aux_dt.getHour(), list);
                day_hour.put(aux_dt.getDayOfMonth(), hour_transactions);
                res.put(aux_dt.getMonthValue(), day_hour);
            }
            return res;
        };

    Supplier<Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>>> forEach_grouper = 
        () -> {
            Map<Integer,Map<Integer,Map<Integer, List<TransCaixa>>>> res = new HashMap<>();;
            Map<Integer, Map<Integer, List<TransCaixa>>> day_hour;
            Map<Integer, List<TransCaixa>> hour_transactions;
            List<TransCaixa> list;
            LocalDateTime aux_dt;

            for(TransCaixa t: transactions){
                aux_dt = t.getData();
                day_hour = res.getOrDefault(aux_dt.getMonthValue(), new HashMap<>());
                hour_transactions = day_hour.getOrDefault(aux_dt.getDayOfMonth(), new HashMap<>());  
                list = hour_transactions.getOrDefault(aux_dt.getHour(), new ArrayList<>());
                list.add(t);
                hour_transactions.put(aux_dt.getHour(), list);
                day_hour.put(aux_dt.getDayOfMonth(), hour_transactions);
                res.put(aux_dt.getMonthValue(), day_hour);
            }
            return res;
        };
    
    if(!sampling){
        bench_results = testeBoxGen(stream_grouper);
        System.out.println("[Sequential Stream] Grouped in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(parallel_stream_grouper);
        System.out.println("[Parallel Stream]   Grouped in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(iterator_grouper);
        System.out.println("[Iterator]          Grouped in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(forEach_grouper);
        System.out.println("[forEach]           Grouped in " + bench_results.getKey() + "s");
    }else{
        System.out.println("[Sequential Stream] Grouped in " + sampler(stream_grouper) + "s");
        System.out.println("[Parallel Stream]   Grouped in " + sampler(parallel_stream_grouper) + "s");
        System.out.println("[Iterator]          Grouped in " + sampler(iterator_grouper) + "s");
        System.out.println("[forEach]           Grouped in " + sampler(forEach_grouper) + "s");
    }
}

    
//-------------------------------------------------------------------------------------------//
//                                           T7                                              //
//-------------------------------------------------------------------------------------------//

public static void T7(List<TransCaixa> transactions, boolean sampling){
    SimpleEntry<Double, Double> bench_results;

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
            spliterator1.forEachRemaining(reduce(0,(ac, t) -> ac + t.getValor()));
            return sum;
        };*/

    Supplier<Double> spliterator_sum_seq_stream =
        () -> {
            Spliterator<TransCaixa> split = (new ArrayList<>(transactions)).spliterator();
            Spliterator<TransCaixa> split2 = split.trySplit();
            Spliterator<TransCaixa> split3 = split.trySplit();
            Spliterator<TransCaixa> split4 = split2.trySplit();
            double sum_1 = StreamSupport.stream(split, false).mapToDouble(TransCaixa::getValor).sum();
            double sum_2 = StreamSupport.stream(split2, false).mapToDouble(TransCaixa::getValor).sum();
            double sum_3 = StreamSupport.stream(split3, false).mapToDouble(TransCaixa::getValor).sum();
            double sum_4 = StreamSupport.stream(split4, false).mapToDouble(TransCaixa::getValor).sum();
            return sum_1 + sum_2 + sum_3 + sum_4;
        };
    
    Supplier<Double> spliterator_sum_parallel_stream =
        () -> {
            Spliterator<TransCaixa> split = (new ArrayList<>(transactions)).spliterator();
            Spliterator<TransCaixa> split2 = split.trySplit();
            Spliterator<TransCaixa> split3 = split.trySplit();
            Spliterator<TransCaixa> split4 = split2.trySplit();
            double sum_1 = StreamSupport.stream(split, true).mapToDouble(TransCaixa::getValor).sum();
            double sum_2 = StreamSupport.stream(split2, true).mapToDouble(TransCaixa::getValor).sum();
            double sum_3 = StreamSupport.stream(split3, true).mapToDouble(TransCaixa::getValor).sum();
            double sum_4 = StreamSupport.stream(split4, true).mapToDouble(TransCaixa::getValor).sum();
            return sum_1 + sum_2 + sum_3 + sum_4;
        };

    if(!sampling){
        bench_results = testeBoxGen(list_sum_list);
        System.out.println("[List]                      Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(list_sum_stream);
        System.out.println("[Stream : Sequential]       Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(list_sum_parallel);
        System.out.println("[Stream : Parallel]         Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(spliterator_sum_seq_stream);
        System.out.println("[Spliterator : Seq Stream]  Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(spliterator_sum_parallel_stream);
        System.out.println("[Spliterator : Par. Stream] Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    }else{
        System.out.println("[List]                      Computed in " + sampler(list_sum_list) + "s");
        System.out.println("[Stream : Sequential]       Computed in " + sampler(list_sum_stream) + "s");
        System.out.println("[Stream : Parallel]         Computed in " + sampler(list_sum_parallel) + "s");
        System.out.println("[Spliterator : Seq Stream]  Computed in " + sampler(spliterator_sum_seq_stream) + "s");
        System.out.println("[Spliterator : Par. Stream] Computed in " + sampler(spliterator_sum_parallel_stream) + "s");
    }
}

//-------------------------------------------------------------------------------------------//
//                                           T8                                              //
// Max value of a filtered Collection                                                        //
//-------------------------------------------------------------------------------------------//

public static void T8(List<TransCaixa> transactions, boolean sampling){
    SimpleEntry<Double, String> bench_results;

    LocalDateTime ld1 = LocalDateTime.of(2017, 02, 20, 16, 0);
    LocalDateTime ld2 = LocalDateTime.of(2017, 02, 20, 22, 0);

    Supplier<String> java_7_biggest_tcode = 
    () -> {
        String r = "";
        double v = Double.MIN_NORMAL;
        for (TransCaixa t : transactions)
            if (t.getData().isAfter(ld1) && t.getData().isBefore(ld2))
                if (t.getValor() > v){ 
                    r = t.getTrans(); 
                    v = t.getValor(); 
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
    () -> {;
        Optional<TransCaixa> tr = transactions.stream()  
                    .filter(t -> t.getData().isAfter(ld1) && t.getData().isBefore(ld2))
                    .max(byValor);
        return tr.get().getTrans();
    };

    Supplier<String> java_8_biggest_tcode_parallel = 
    () -> {
        Optional<TransCaixa> tr = transactions.parallelStream()  
                    .filter(t -> t.getData().isAfter(ld1) && t.getData().isBefore(ld2))
                    .max(byValor);
        return tr.get().getTrans();
    };

    if(!sampling){
        bench_results = testeBoxGen(java_7_biggest_tcode);
        System.out.println("[Java 7]              Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(java_8_biggest_tcode);
        System.out.println("[Stream : Sequential] Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(java_8_biggest_tcode_parallel);
        System.out.println("[Stream : Parallel]   Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    }else{
        System.out.println("[Java 7]              Computed in " + sampler(java_7_biggest_tcode) + "s");
        System.out.println("[Stream : Sequential] Computed in " + sampler(java_8_biggest_tcode) + "s");
        System.out.println("[Stream : Parallel]   Computed in " + sampler(java_8_biggest_tcode_parallel) + "s");
    }

}

//-------------------------------------------------------------------------------------------//
//                                           T9                                              //
// Sum of transactions made during a week period                                             //
//-------------------------------------------------------------------------------------------//

public static void T9(List<TransCaixa> transactions, boolean sampling) {
    SimpleEntry<Double,List<Double>> faturado_bench_results;
    List<List<TransCaixa>> week_transactions = transactions.stream()
                                                           .collect(groupingBy(t->t.getData().toLocalDate().get(ChronoField.ALIGNED_WEEK_OF_YEAR)))
                                                           .values()
                                                           .stream()
                                                           .collect(toList());

    Supplier<List<Double>> stream_seq_faturado =
        () -> {
            return week_transactions.stream()
                                    .mapToDouble(listasemana-> listasemana.stream()
                                                                    .mapToDouble(t->t.getValor())
                                                                    .sum())
                                    .boxed()
                                    .collect(Collectors.toList());
        };
    
    Supplier<List<Double>> stream_parallel_faturado =
        () -> {
            return week_transactions.stream()
                                    .parallel()
                                    .mapToDouble(listasemana-> listasemana.stream()
                                                                    .mapToDouble(t->t.getValor())
                                                                    .sum())
                                    .boxed()
                                    .collect(Collectors.toList());
        };
    
    Supplier<List<Double>> java_7_faturado =
        () -> {
            double week_sum = 0.f;
            List<Double> semana_faturado = new ArrayList<>();

            for (List<TransCaixa> semana:week_transactions) {
                week_sum = 0.f;
                for (TransCaixa t : semana)
                    week_sum += t.getValor();
                semana_faturado.add(week_sum);
            }
            return semana_faturado;
        };

    if(!sampling){
        faturado_bench_results = testeBoxGen(stream_seq_faturado);
        System.out.println("[Stream : Sequential] Computed in " + faturado_bench_results.getKey() + "s");
        faturado_bench_results = testeBoxGen(stream_parallel_faturado);
        System.out.println("[Stream : Parallel]   Computed in " + faturado_bench_results.getKey() + "s");
        faturado_bench_results = testeBoxGen(java_7_faturado);
        System.out.println("[forEach]             Computed in " + faturado_bench_results.getKey() + "s");
    }else{
        System.out.println("[Stream : Sequential] Computed in " + sampler(stream_seq_faturado) + "s");
        System.out.println("[Stream : Parallel]   Computed in " + sampler(stream_parallel_faturado) + "s");
        System.out.println("[forEach]             Computed in " + sampler(java_7_faturado) + "s");
    }
}

//-------------------------------------------------------------------------------------------//
//                                           T10                                             //
// Monthly IVA value                                                                         //
//-------------------------------------------------------------------------------------------//

public static double get_iva(double valor){
    if (valor < 20) return 0.12;
    else if (valor < 29) return 0.20;
    else return 0.23;
}

public static void T10(List<TransCaixa> transactions, boolean sampling){
    SimpleEntry<Double, Map<Integer, Double>> bench_results;

    Supplier<Map<Integer, Double>> java_7_iva = 
        () -> {
            Map<Integer, Double> monthly_iva = new HashMap<>();
            int month;
            double valor, sum;

            for (TransCaixa t : transactions){
                month = t.getData().getMonthValue();
                sum = monthly_iva.getOrDefault(month, 0.0);
                valor = t.getValor();
                sum += valor * get_iva(valor);
                monthly_iva.put(month, sum);
            }
            
            return monthly_iva;
        };

    Supplier<Map<Integer, Double>> stream_seq_iva = 
        () -> {
            return transactions.stream()
                                .collect(
                                    groupingBy(t -> t.getData().getMonthValue(), 
                                                summingDouble(t -> {
                                                    double valor = t.getValor();
                                                    return valor*get_iva(valor);
                                                })));
        };

    Supplier<Map<Integer, Double>> stream_parallel_iva = 
        () -> {
            return transactions.stream()
                               .parallel()
                               .collect(
                                    groupingBy(t -> t.getData().getMonthValue(), 
                                                summingDouble(t -> {
                                                    double valor = t.getValor();
                                                    return valor*get_iva(valor);
                                                })));
        };

    if(!sampling){
        bench_results = testeBoxGen(java_7_iva);
        System.out.println("[forEach : Java7]     Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(stream_seq_iva);
        System.out.println("[Stream : Sequential] Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
        bench_results = testeBoxGen(stream_parallel_iva);
        System.out.println("[Stream : Parallel]   Computed " + bench_results.getValue() + " in " + bench_results.getKey() + "s");
    }else{
        System.out.println("[forEach : Java7]     Computed in " + sampler(java_7_iva) + "s");
        System.out.println("[Stream : Sequential] Computed in " + sampler(stream_seq_iva) + "s");
        System.out.println("[Stream : Parallel]   Computed in " + sampler(stream_parallel_iva) + "s");
    }

}

//-------------------------------------------------------------------------------------------//
//                                           T11                                             //
//-------------------------------------------------------------------------------------------//
public static void T11(List<TransCaixa> transactions, boolean sampling) {
    T2(transactions, sampling);
}

//-------------------------------------------------------------------------------------------//
//                                           T12                                              //
//-------------------------------------------------------------------------------------------//
public static void T12(List<TransCaixa> transactions, boolean sampling) {
    SimpleEntry<Double, Map<String,Map<Integer,List<TransCaixa>>>> map_bench_results;
    SimpleEntry<Double, ConcurrentMap<String,ConcurrentMap<Integer,List<TransCaixa>>>> concmap_bench_results;
    
    SimpleEntry<Double,Map<String,Double>> total_faturado_map;
    SimpleEntry<Double,ConcurrentMap<String,Double>> total_faturado_conc;

    Supplier<Map<String,Map<Integer,List<TransCaixa>>>> map =
            () -> {
                return transactions.stream()
                                   .collect(groupingBy(t->t.getCaixa(),
                                                       groupingBy(t->t.getData().getMonthValue())));
            };

    Supplier<ConcurrentMap<String,ConcurrentMap<Integer,List<TransCaixa>>>> concMap =
            () -> {
                return transactions.parallelStream()
                                   .collect(groupingByConcurrent(t->t.getCaixa(),
                                                                 groupingByConcurrent(t->t.getData().getMonthValue())));
            };

    Supplier<Map<String,Double>> fat_map =
            () -> {
                return transactions.stream()
                        .collect(groupingBy(t->t.getCaixa(),
                                summingDouble(t->t.getValor())));
            };

    Supplier<ConcurrentMap<String,Double>> fat_conc_map =
            () -> {
                return transactions.parallelStream()
                        .collect(groupingByConcurrent(t->t.getCaixa(),
                                summingDouble(t->t.getValor())));
            };

    
    if(!sampling){
        map_bench_results = testeBoxGen(map);
        System.out.println("[Map] Computed in " + map_bench_results.getKey() + "s");
        concmap_bench_results = testeBoxGen(concMap);
        System.out.println("[Concurrent Map] Computed in " + concmap_bench_results.getKey() + "s");
        total_faturado_map = testeBoxGen(fat_map);
        System.out.println("[Map] Computed in " + total_faturado_map.getKey() + "s");
        total_faturado_conc = testeBoxGen(fat_conc_map);
        System.out.println("[Concurrent Map] Computed in " + total_faturado_conc.getKey() + "s");
    }else{
        System.out.println("[Map] Computed in " +  sampler(map) + "s");
        System.out.println("[Concurrent Map] Computed in " +  sampler(concMap) + "s");
        System.out.println("[Map] Computed in " +  sampler(fat_map) + "s");
        System.out.println("[Concurrent Map] Computed in " +  sampler(fat_conc_map) + "s");
    }
}

//-------------------------------------------------------------------------------------------//
//                                     TESTE_BOX_GEN                                         //
//-------------------------------------------------------------------------------------------//

    private static <R> SimpleEntry<Double,R> testeBoxGen(Supplier<? extends R> supplier) {
        for(int i = 0; i < 10; i ++) supplier.get();    //warmup caches
        System.gc();                                    //request garbage collector
        Crono.start();
        R resultado = supplier.get();
        Double tempo = Crono.stop();
        return new SimpleEntry<Double,R>(tempo, resultado);
    }

    private static <R> Double sampler(Supplier<? extends R> supplier) {
        for(int i = 0; i < 10; i ++) supplier.get();    //warmup caches
        System.gc();                                    //request garbage collector
        Double tempo;
        int pivot;
        R resultado;
        List<Double> samples = new ArrayList<>(); 
        
        for(int i = 0; i < 15; i ++){
            Crono.start();
            resultado = supplier.get();
            tempo = Crono.stop();
            samples.add(tempo);
        }

        pivot = samples.size() / 2;

        if(samples.size() % 2 == 0)
            return samples.get(pivot);
        else return (samples.get(pivot) + samples.get(pivot+1))/2;
    }
}