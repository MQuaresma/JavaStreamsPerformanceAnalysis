/**
 *
 * @author fmm 2017
 */

import java.time.LocalDateTime;


public class TransCaixa {
    
    public static TransCaixa of(String ct, String cx, double val, LocalDateTime dt) {
        // LocalDateTime dt assume-se como válido
        return new TransCaixa(ct, cx, val, dt);
    }
    
    private TransCaixa(String ct, String cx, double val, LocalDateTime dt) {
        codTrans = ct; caixa = cx; valor = val; data = dt;
    }
    
    private final String codTrans;
    private final String caixa;
    private final double valor;
    private final LocalDateTime data;
    //
    public String getTrans() { return codTrans; }
    public String getCaixa() { return caixa; }
    public double getValor() { return valor; }
    public LocalDateTime getData() { return data; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trans: " + codTrans + "/" + caixa + "/" + valor + "/" + data.toString());
        return sb.toString();
    } 
}
    
    