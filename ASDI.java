import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASDI implements Parser{
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;
    
    Deque<Object> pila = new ArrayDeque<>();

    public ASDI(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);

        pila.push(TipoToken.EOF);
        pila.push("Q");
    }

    @Override
    public boolean parse() {
        while(!hayErrores && !pila.isEmpty()){
            if(pila.peek() instanceof TipoToken){
                TipoToken Terminal = (TipoToken) pila.peek();
                switch (Terminal) {
                    case EOF:
                        popAndPush(null);
                        break;
                    case IDENTIFICADOR:
                    case FROM:
                        match(Terminal);
                        popAndPush(null);
                        break;
                }
            }
            else if(pila.peek() instanceof String){
                String noTerminal = (String) pila.peek();
                switch (noTerminal) {
                    case "Q":
                        Q();
                        break;
                    case "D":
                        D();
                        break;
                    case "P":
                        P();
                        break;
                    case "A":
                        A();
                        break;
                    case "A1":
                        A1();
                        break;
                    case "A2":
                        A2();
                        break;
                    case "A3":
                        A3();
                        break;
                    case "T":
                        T();
                        break;
                    case "T1":
                        T1();
                        break;
                    case "T2":
                        T2();
                        break;
                    case "T3":
                        T3();
                        break;
                }
            }
        }
        if(!hayErrores){
            System.out.println("La consulta es correcta.");
        }
        return !hayErrores;
    }

    private void Q(){
        matchErrores(TipoToken.SELECT);
        popAndPush(List.of("D", TipoToken.FROM,"T"));
    }

    private void D(){
        switch (preanalisis.tipo) {
            case DISTINCT:
                match(TipoToken.DISTINCT);
                popAndPush(List.of("P"));
                break;
            case ASTERISCO:
            case IDENTIFICADOR:
                popAndPush(List.of("P"));
                break;
            default:
                hayErrores = true;
                System.out.println("Error de sintaxis: Se esperaba DISTINCT, ASTERISCO, IDENTIFICADOR");
                break;
        }
    }

    private void P(){
        switch (preanalisis.tipo) {
            case IDENTIFICADOR:
                popAndPush(List.of("A"));
                break;
            case ASTERISCO:
                match(TipoToken.ASTERISCO);
                popAndPush(null);
                break;
            default:
                hayErrores = true;
                System.out.println("Error de sintaxis: Se esperaba ASTERISCO, IDENTIFICADOR");
                break;
        }
    }

    private void A(){
        System.out.println(1);
        popAndPush(List.of("A2", "A1"));
    }

    private void A1(){
        switch (preanalisis.tipo) {
            case FROM:
                popAndPush(null);;
                break;
            case COMA:
                match(TipoToken.COMA);
                popAndPush(List.of("A"));
                break;
            default:
                hayErrores = true;
                System.out.println("Error de sintaxis: Se esperaba FROM, COMA");
                break;
        }
    }

    private void A2(){
        matchErrores(TipoToken.IDENTIFICADOR);
        System.out.println(2);
        popAndPush(List.of("A3"));
    }

    private void A3(){
        switch (preanalisis.tipo) {
            case PUNTO:
                match(TipoToken.PUNTO);
                popAndPush(List.of(TipoToken.IDENTIFICADOR));
                break;
            case FROM:
            case COMA:
                popAndPush(null);
                break;
            default:
                hayErrores = true;
                System.out.println("Error de sintaxis: Se esperaba IDENTIFICADOR, FROM, COMA");
                break;
        }
    }

    private void T(){
        System.out.println(3);
        popAndPush(List.of("T2", "T1"));
    }

    private void T1(){
        switch (preanalisis.tipo) {
            case EOF:
                popAndPush(null);
                break;
            case COMA:
                match(TipoToken.COMA);
                popAndPush(List.of("T"));
                break;
            default:
                hayErrores = true;
                System.out.println("Error de sintaxis: Se esperaba EOF, COMA");
                break;
        }
    }

    private void T2(){
        matchErrores(TipoToken.IDENTIFICADOR);
        System.out.println(4);
        popAndPush(List.of("T3"));
    }

    private void T3(){
        switch (preanalisis.tipo) {
            case IDENTIFICADOR:
                match(TipoToken.IDENTIFICADOR);
                popAndPush(null);
                break;
            case COMA:
            case EOF:
                popAndPush(null);
                break;
            default:
                hayErrores = true;
                System.out.println("Error de sintaxis: Se esperaba IDENTIFICADOR, EOF, COMA");
                break;
        }
    }

    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
        }
    }

    private void matchErrores(TipoToken tt){
        if(!hayErrores){
            match(tt);
            if (hayErrores) {
                System.out.println("Error de sintaxis: Se esperaba " + tt);
            }
        }
    }

    private void popAndPush(List<Object> elementos){
        pila.pop();
        if (elementos != null)
            for (int i = elementos.size() - 1; i >= 0; i--)
                pila.push(elementos.get(i));
    }

}
