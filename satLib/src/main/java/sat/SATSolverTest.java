package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import java.io.*;
import java.util.*;

import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();


	// MAIN
    public static void main(String[] args) {

        try {
            File file = new File(args[0]);
            FileReader fReader  = new FileReader(file);
            BufferedReader br =  new BufferedReader(fReader);
            String line;
            Integer counter = 0;
            Formula f = new Formula();

            while((line = br.readLine())!=null){

                if (line.equals("") || line.charAt(0) == 'c' || line.charAt(0) == 'p') {counter++;continue;}

                String[] literalArray = line.split("\\s+");

                Clause newClause = new Clause();

                for (String literal : literalArray){

                    if (literal.equals("0") || literal.equals("")) continue;

                    if (literal.contains("-")){
                        Literal newLiteral = NegLiteral.make(literal.replace("-", ""));
                        newClause = newClause.add(newLiteral);
                    }

                    else {
                        Literal newLiteral = PosLiteral.make(literal);
                        newClause = newClause.add(newLiteral);
                    }
                }
                if (!newClause.isEmpty()){f = f.addClause(newClause);}
            }

            System.out.println("SAT solver starts!!!");
            long started = System.nanoTime();
            Environment output = SATSolver.solve(f);
            long time = System.nanoTime();
            long timeTaken= time - started;
            System.out.println("Time:" + timeTaken/1000000.0 + "ms");

            File outputFile = new File("BoolAssignment.txt");

            try (Writer writeFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))){

                String s = output.toString().replace("Environment:[", "");
                s = s.replace("]", "");
                String[] lines = s.split(", ");
                for (String l: lines) {
                    l = l.replace("->",":");
                    writeFile.write(l + "\r\n");
                }
                writeFile.close();
                System.out.println("File Created");
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            if (output != null) {
                System.out.println("satisfiable");
            } else {
                System.out.println("unsatisfiable");
            }
        }

        catch (Exception FileNotFoundException){
            System.out.println("FileNotFoundException");
        }



    }


    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }


    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }


    
}