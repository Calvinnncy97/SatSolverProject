package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment env = new Environment();
//        System.out.println("FORMULA SIZE: " + formula.getSize());
        return solve(formula.getClauses(), env);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // clauses is the list in CNF format, env is the f(T,T,F,F,T) = (TorT)and(ForF)and(T)
        // Find the combination of env example-f(T,T,F,F,T) that will evaluate to Bool.TRUE

//        System.out.println(env.toString());

        if (clauses.isEmpty()) {
            // No clauses, trivially satisfiable
            return env;
        } else {

            Clause shortestClause = new Clause();

            for (Clause clause : clauses) {
                if (clause.isEmpty()) {
                    return null;
                } else {
                    if (shortestClause.isEmpty() || clause.size() < shortestClause.size()) {
                        shortestClause = clause;
                    }
                }
            }
//            System.out.println(shortestClause);

            Literal literal = shortestClause.chooseLiteral();
//            System.out.println(literal);
            Variable var = shortestClause.chooseLiteral().getVariable();

            // Check if its negated, if so, negate once more to positive
//            if (literal.equals(NegLiteral.make(var))) {
//                literal = literal.getNegation();
//            }

            if (shortestClause.isUnit()) {
                env = env.putTrue(var);         // env is different from the Imlist<Clauses> // (a,b,c,d,e) -> make a to TRUE -> (T,b,c,d,e)    // Clauses CNF = (T) and (...)
                ImList<Clause> newClauses = substitute(clauses, literal);
                return solve(newClauses, env);
            } else {
                // Not unit Clause
                env = env.putTrue(var);
                ImList<Clause> newClauses = substitute(clauses, literal);

                Environment updatedEnv = solve(newClauses, env);

                if (updatedEnv == null) {
                    env = env.putFalse(var);
                    ImList<Clause> newClausesF = substitute(clauses, literal.getNegation());
                    return solve(newClausesF, env);

                } else {
                    return updatedEnv;
                }
            }
        }
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {

        ImList<Clause> newListOfClauses = new EmptyImList<>();

        for (Clause clause : clauses) {

            if (clause.contains(l) || clause.contains(l.getNegation())) {
                Clause reducedClause = clause.reduce(l);

                if (reducedClause != null) {
                    newListOfClauses = newListOfClauses.add( reducedClause );       // returns clause by setting literal to true
                }
            } else {            // Clause does not contain literal
                newListOfClauses = newListOfClauses.add(clause);
            }
        }

        return newListOfClauses;
    }
}
