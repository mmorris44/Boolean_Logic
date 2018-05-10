// Matthew Morris
// Statement manipulation

import java.util.*;

class Statement{
   //Is simply a true or false concept
   String id;
   String meaning;
   boolean result,isProven;
   boolean hasFlaw = false; //true if this statement has already been flagged by the contradiction calculate command
   
   public Statement(String meaning,String id){
      this.meaning = meaning;
      isProven = false;
      this.id = id;
   }
   
   public Statement(String meaning, String id, boolean result){
      this(meaning,id);
      this.isProven = true;
      this.result = result;
   }
   
   void setResult(boolean b){ 
      //Set result of statement
      result = b;
      isProven = true;
   }
   
   public String toString(){ 
      //Returns statement as a string
      String append = "Is proven: " + isProven;
      if (isProven){
         append = "Result: " + result;
      }
      return ("ID: " + id + " | Meaning: '" + meaning + "' | " + append);
   }
}

class Relation extends Statement{
   //This defines a relationship between two statements
   Statement from,to;
   RelationType relation;
   
   public Relation(String meaning, String id, Statement from, Statement to, RelationType relation){
      super(meaning, id);
      this.from = from;
      this.to = to;
      this.relation = relation;
   }
   
   public Relation(String meaning, String id, boolean result, Statement from, Statement to, RelationType relation){
      super(meaning, id, result);
      this.from = from;
      this.to = to;
      this.relation = relation;
   }
   
   public Relation(){
      super("1","defualt");
   }
   
   public String toString(){ 
      //returns a string for the relation
      return (super.toString() + " | " + "From " + from.id + " to " + to.id);
   }
   
   void update(){ 
      //this updates the value of the relation based on the two boolean inputs
      if(from.isProven && to.isProven){
         result = relation.getResult(from.result,to.result);
         isProven = true;
      }else{
         System.out.println("Statements must be proven for update for relation '" + meaning + "'");
      }
   }
   
   void updateResult(char side){ 
      //given a side, this updates
      if (this.isProven){ //if relation proven
         if (side=='l'){
            if (to.isProven){ //if we know r
               Result resultTemp = relation.getResultOf('l',result,to.result);
               if (!resultTemp.error){
                  from.result = resultTemp.result;
                  from.isProven = true;
               }
            }else{
               System.out.println("Statements must be proven for updateValue for relation '" + id + "'");
            }
         }else if(side=='r'){
            if (from.isProven){ //if we know left
               Result resultTemp = relation.getResultOf('r',result,from.result);
               if (!resultTemp.error){
                  to.result = resultTemp.result;
                  to.isProven = true;
               }
            }else{
               System.out.println("Statements must be proven for updateValue for relation '" + id + "'");
            }
         }else{
            System.out.println("Enter 'r' or 'l' for updateValue");
         }
      }else{
         System.out.println("The relation must be proven for updateValue for relation '" + id + "'");
      }
   }
}

class RelationType{
   //This defines how a certain relation operates
   static final RelationType 
   AND = new RelationType(true,false,false,false),
   OR = new RelationType(true,true,true,false),
   IMPLIES = new RelationType(true,false,true,true),
   NAND = new RelationType(false,true,true,true),
   NOR = new RelationType(false,false,false,true),
   NIMPLIES = new RelationType(false,true,false,false);
   
   boolean tt,tf,ft,ff;
   public RelationType(boolean tt, boolean tf, boolean ft, boolean ff){ 
      //allows making of custom RelationType
      this.tt = tt;
      this.tf = tf;
      this.ft = ft;
      this.ff = ff;
   }
   
   boolean getResult(boolean a, boolean b){
      //Returns the result of the relation given the two inputs
      if (a && b){
         return tt;
      }else if(a && !b){
         return tf;
      }else if(!a && b){
         return ft;
      }
      return ff;
   }
   
   Result getResultOf(char side, boolean result, boolean other){
      //Determines the result of one of the statements inside the relation
      if (side=='l'){
         boolean potential1 = false;
         boolean potential2 = true;
         if (getResult(true,other)==result){
            potential1 = true;
         }
         if (getResult(false,other)==result){
            potential2 = false;
         }
         if (potential1==potential2){
            return new Result(potential1,false);
         }
         return new Result(true,true);
      }
      if (side=='r'){
         boolean potential1 = false;
         boolean potential2 = true;
         if (getResult(other,true)==result){
            potential1 = true;
         }
         if (getResult(other,false)==result){
            potential2 = false;
         }
         if (potential1==potential2){
            return new Result(potential1,false);
         }
         return new Result(true,true);
      }
      System.out.println("Enter 'l' or 'r' for getResultOf");
      return new Result(true,true);
   }
}

class Result{
   //stores a result and whether there was an error or not
   boolean result,error;
   
   public Result(boolean result, boolean error){
      this.result = result;
      this.error = error;
   }
}

public class Algebra{
   public static void main(String[] args){
      Statement a = new Statement("Is an animal","a");
      Statement b = new Statement("Is a cat","b");
      Statement c = new Statement("Breathes","c");
      Statement d = new Statement("Is alive","d");
      Relation r = new Relation("Cats are animals","r",true,b,a,RelationType.IMPLIES);
      Relation u = new Relation("Animals and breathing","s",a,c,RelationType.AND);
      Relation t = new Relation("Meaning it's alive","t",true,u,d,RelationType.IMPLIES);
      
      b.setResult(true); //is a cat
      c.setResult(true); //is breathing
      r.updateResult('r'); //cat implies animal
      u.update(); //animal and breathing proven
      t.updateResult('r'); //is alive proven
      
      System.out.println("Current version is working: " + d.result);
      ArrayList<Statement> statements = new ArrayList<Statement>();
      Scanner s = new Scanner(System.in);
      
      System.out.println("********** Welcome to logiK **********");
      String input = s.nextLine();
      while(!input.equals("quit")){
         try{
         String[] parts = input.split(" ");
            switch (parts[0]){
               case "help":
                  System.out.println("The following commands can be used: (note: '(' means optional)");
                  System.out.println("1. statement <id> '<meaning>' (-t/f)- creates a new statement");
                  System.out.println("2. relation <id> <from_id> <to_id> <relation type (*tt tf ft ff*)> <meaning> (-t/f) - creates a new relation");
                  System.out.println("3. calculate - tries to calculate all values of unknown statements");
                  System.out.println("4. result <id> - displays the result of a specific statement");
                  System.out.println("5. set <id> t/f/n - sets a statement to true, false or not proven");
                  System.out.println("6. delete <id> - deletes the statement with the given id");
                  System.out.println("7. display - prints out details of all statements");
                  System.out.println("8. quit - terminates the program");
                  break;
               
               case "statement":
                  String id = parts[1];
                  parts = input.split("'");
                  String meaning = parts[1];
                  boolean exists = false; //check existence
                  for (int i=0; i<statements.size(); i++){
                     if (statements.get(i).id.equals(id)){
                        exists = true;
                        break;
                     }
                  }
                  if (exists){
                     System.out.println("A statement of that id already exists");
                  }else{
                     parts = input.split("-");
                     if (parts.length == 1){ //look for true/false enforcement
                        statements.add(new Statement(meaning,id));
                     }
                     else{
                        boolean result = false;
                        if (parts[1].equals("t")){
                           result = true;
                        }else if(parts[1].equals("f")){
                           result = false;
                        }else{
                           System.out.println("Tag must be either 't' or 'f'");
                           break;
                        }
                        statements.add(new Statement(meaning,id,result));
                     }
                  }
                  break;
               
               case "relation":
                  id = parts[1];
                  String relationType = parts[4];
                  exists = false;
                  for (int i=0; i<statements.size(); i++){
                     if (statements.get(i).id.equals(id)){
                        exists = true;
                        break;
                     }
                  }
                  if (exists){
                     System.out.println("A statement of that id already exists");
                     break;
                  }
                  String from_id = parts[2], to_id = parts[3];
                  int from_index = -1, to_index = -1;
                  for (int i=0; i<statements.size(); i++){
                     if (statements.get(i).id.equals(from_id)){
                        from_index = i;
                     }
                     if (statements.get(i).id.equals(to_id)){
                        to_index = i;
                     }
                  }
                  if (from_index == -1 || to_index == -1){
                     System.out.println("The relation must be between statements you have already defined");
                     break;
                  }
                  parts = input.split("'");
                  if (parts.length>1){
                     meaning = parts[1];
                  }else{
                     System.out.println("Single quotation marks must be properly used around the meaning");
                     break;
                  }
                  
                  parts = input.split("\\*");
                  RelationType relation = new RelationType(false,false,false,false);
                  boolean isCustom = false;
                  boolean error = false; //true if relation not recognised
                  if (parts.length > 1){
                     isCustom = true;
                     String custom = parts[1];
                     parts = custom.split(" ");
                     boolean[] tf = new boolean[4];
                     for (int i=0; i<4; i++){
                        if (parts[i].equals("t")){
                           tf[i] = true;
                        }else if (parts[i].equals("f")){
                           tf[i] = false;
                        }
                     }
                     relation = new RelationType(tf[0],tf[1],tf[2],tf[3]);
                  }else{
                     switch (relationType){
                        case "and":
                           relation = RelationType.IMPLIES;
                           break;
                        case "or":
                           relation = RelationType.OR;
                           break;
                        case "implies":
                           relation = RelationType.IMPLIES;
                           break;
                        case "nand":
                           relation = RelationType.NAND;
                           break;
                        case "nor":
                           relation = RelationType.NOR;
                           break;
                        case "nimplies":
                           relation = RelationType.NIMPLIES;
                           break;
                        default:
                           System.out.println("'" + relationType + "' is not a recognised relation");
                           error = true;
                           break;
                     }
                  }
                  if (error){
                     break;
                  }
                  
                  parts = input.split("-");
                  if (parts.length==1){
                     statements.add(new Relation(meaning, id, statements.get(from_index), statements.get(to_index), relation));
                  }else{
                     boolean result = false;
                     if (parts[1].equals("t")){
                        result = true;
                     }else if(parts[1].equals("f")){
                        result = false;
                     }else{
                        System.out.println("Tag must be either 't' or 'f'");
                        break;
                     }
                     statements.add(new Relation(meaning, id, result, statements.get(from_index), statements.get(to_index), relation));
                  }
                  
                  break;
               
               case "calculate":
                  System.out.println("\n---Calculations done---");
                  for (int q=0; q<statements.size(); q++){ //repeat for howevever many statements there are
                     for (int i=0; i<statements.size(); i++){
                        Statement current = statements.get(i);
                        
                        if (
                        current.getClass().equals(Relation.class) //is a relation
                        && ((Relation)current).from.isProven //from statement is proven
                        && ((Relation)current).to.isProven //to statement is proven
                        ){ 
                           if (current.isProven){ //then check for internal flaws in logic setup
                              Relation currentRelation = (Relation)current;
                              boolean currentResult = currentRelation.result;
                              currentRelation.update();
                              if (currentResult != currentRelation.result && !currentRelation.hasFlaw){ //flaw found not already flagged
                                 currentRelation.hasFlaw = true; //flag flaw
                                 System.out.println("Contradiction found in relation " + current.id);
                                 System.out.println("Relation was defined as " + currentResult + " but when calculated returned " + currentRelation.result);
                                 System.out.println();
                              }
                              currentRelation.result = currentResult; //reset the result to original one
                           }
                           else{ //then try get a new value for the relation
                              Relation currentRelation = (Relation)current;
                              currentRelation.update();
                              System.out.println(current.id + " - '" + current.meaning + "' was proven to be " + current.result + " by statements " + currentRelation.from.id + " and " + currentRelation.to.id);
                           }
                        }
                        
                        if (
                        current.getClass().equals(Relation.class) //is a relation
                        && current.isProven //is a proven statement
                        && ((Relation)current).from.isProven //from is proven
                        && !((Relation)current).to.isProven //to is not proven
                        ){
                           //try to infer the value of the right side
                           Relation currentRelation = (Relation)current;
                           currentRelation.updateResult('r');
                           if (currentRelation.to.isProven){
                              System.out.println(currentRelation.to.id + " - '" + current.meaning + "' was proven to be " + currentRelation.to.result + " with relation " + currentRelation.id);
                           }
                        }
                        
                        if (
                        current.getClass().equals(Relation.class) //is a relation
                        && current.isProven //is a proven statement
                        && !((Relation)current).from.isProven //from is not proven
                        && ((Relation)current).to.isProven //to is proven
                        ){
                           //try to infer the value of the left side
                           Relation currentRelation = (Relation)current;
                           currentRelation.updateResult('l');
                           if (currentRelation.from.isProven){
                              System.out.println(currentRelation.from.id + " - '" + currentRelation.from.meaning + "' was proven to be " + currentRelation.from.result + " through relation " + currentRelation.id);
                           }
                        }
                     }
                  }
                  //Remove all flags
                  for (int i=0; i<statements.size(); i++){
                     statements.get(i).hasFlaw = false;
                  }
                  System.out.println();
                  break;
               
               case "result":
                  int index = 0;
                  id = parts[1];
                  for (int i=0; i<statements.size(); i++){ //find statement
                     if (statements.get(i).id.equals(id)){
                        index = i;
                     }
                  }
                  if (statements.get(index).isProven){ //check if proven
                     System.out.println(statements.get(index).result);
                  }else{
                     System.out.println("Not proven");
                  }
                  break;
               
               case "set":
                  id = parts[1];
                  index = 0;
                  boolean broken = false;
                  boolean result = false;
                  boolean notProven = false;
                  if (parts[2].equals("t")){ //check t or f
                     result = true;
                  }else if(parts[2].equals("f")){
                     result = false;
                  }else if(parts[2].equals("n")){
                     notProven = true;
                  }else{
                     System.out.println("'t' or 'f' must be used to show true or false, or 'n' for not proven");
                     break;
                  }
                  for (int i=0; i<statements.size(); i++){ //find statement, check if statement was actually found
                     if (statements.get(i).id.equals(id)){
                        index = i;
                        broken = true;
                        break;
                     }
                  }
                  if (!broken){ //if not found
                     System.out.println("No statement with that id was found");
                     break;
                  }
                  if (!notProven){ //if told not not proven
                     statements.get(index).setResult(result);
                  }else{
                     statements.get(index).isProven = false;
                  }
                  break;
                  
               case "delete":
                  id = parts[1];
                  index = 0;
                  broken = false;
                  for (int i=0; i<statements.size(); i++){ //find statement
                     if (statements.get(i).id.equals(id)){
                        index = i;
                        broken = true;
                        break;
                     }
                  }
                  if (!broken){ //check if found
                     System.out.println("No statement with that id was found");
                     break;
                  }
                  statements.remove(index);
                  break;
               
               case "display":
                  System.out.println("\n---Statements---"); //print statements
                  for (int i=0; i<statements.size(); i++){
                     if (statements.get(i).getClass().equals(Statement.class)){
                        System.out.println(statements.get(i).toString());
                     }
                  }
                  System.out.println("---Relations---"); //print relations
                  for (int i=0; i<statements.size(); i++){
                     if (statements.get(i).getClass().equals(Relation.class)){
                        System.out.println(statements.get(i).toString());
                     }
                  }
                  System.out.println();
                  break;
                  
               default:
                  System.out.println("Input not recognised, try 'help' for info on commands");
               
            }
         }catch(Exception e){ //catches errors with splits
            System.out.println("You have made a syntax error in your input, try 'help' for more info");
         }
         input = s.nextLine();
      }
      s.close();
      System.out.println("********** Terminated successfully **********");
   }
}
