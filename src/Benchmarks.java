import java.util.List;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.AbstractMap.SimpleEntry;

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
    public static <R> SimpleEntry<Double,R> testeBoxGen(Supplier<? extends R> supplier) {
        for(int i = 0; i < 10; i ++) supplier.get();    //warmup caches
        System.gc();                                    //request garbage collector
        Crono.start();
        R resultado = supplier.get();
        Double tempo = Crono.stop();
        return new SimpleEntry<Double,R>(tempo, resultado);
    }
}