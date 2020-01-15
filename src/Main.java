import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    private static LinkedHashMap<String, String> questions = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> mistakes = new LinkedHashMap<>();
    private static StringBuilder logger = new StringBuilder();
    private static String exportOnEndFile;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        for(int i = 0; i < args.length/2; i++){
            if("-export".equals(args[i*2])){
                exportOnEndFile = args[i*2+1];
            } else if("-import".equals(args[i*2])){
                importQuestions(new File(args[i*2+1]));
            }
        }

        String askAboutCommand = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";

        String command;
        print(askAboutCommand);

        while (sc.hasNext()){
            command = input(sc).trim();

            if("add".equals(command)){

                print("The card:");
                String card = input(sc);
                if (questions.containsKey(card)){
                    print("The card \"" + card + "\" already exists.");
                } else {
                    print("The definition of the card:");
                    String definition = input(sc);
                    if (questions.containsValue(definition)){
                        print("The definition \"" + definition + "\" already exists.");
                    } else {

                        questions.put(card, definition);
                        mistakes.put(card, 0);
                        print("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
                    }
                }

            } else if("remove".equals(command)){

                print("The card:");
                String card = input(sc);
                if (questions.containsKey(card)){
                    questions.remove(card);
                    mistakes.remove(card);
                    print("The card has been removed.");
                } else {
                    print("Can't remove \"" + card + "\": there is no such card.");
                }
            } else if("import".equals(command)){
                print("File name:");
                String file = input(sc);
                importQuestions(new File(file));
            } else if("export".equals(command)){
                print("File name:");
                String file = input(sc);
                exportQuestions(new File(file));
            } else if("ask".equals(command)){
                print("How many times to ask?:");
                int count = Integer.parseInt(input(sc));
                while (count != 0){
                    for(Map.Entry<String, String > entry : questions.entrySet()){
                        if(count == 0){
                            break;
                        }
                        String term = entry.getKey();
                        String definition = entry.getValue();
                        print("Print the definition of \"" + term + "\":");
                        String answer = input(sc);
                        if(answer.toLowerCase(Locale.ENGLISH).equals(definition.toLowerCase(Locale.ENGLISH))){
                            print("Correct answer.");
                        } else {
                            if(questions.containsValue(answer)){
                                String anotherAnswer = "";
                                for(Map.Entry<String, String> e : questions.entrySet()){
                                    if(e.getValue().equals(answer)){
                                        anotherAnswer = e.getKey();
                                        break;
                                    }
                                }
                                mistakes.put(term, mistakes.get(term)+1);
                                print("Wrong answer. The correct one is \"" + definition + "\", you've just written the definition of \"" + anotherAnswer + "\".");
                            } else {
                                mistakes.put(term, mistakes.get(term)+1);
                                print("Wrong answer. The correct one is \"" + definition + "\".");
                            }
                        }
                        count -= 1;
                    }
                }
            } else if("log".equals(command)){
                print("File name:");
                String file = input(sc);
                saveLog(new File(file));
            } else if("hardest card".equals(command)){
                int max = -1;
                for(Integer i : mistakes.values()){
                    if(i > max){
                        max = i;
                    }
                }
                if(max == 0 || max == -1) {
                    print("There are no cards with errors.");
                } else {
                    List<String> worst = new ArrayList<>();
                    for(Map.Entry<String, Integer> e : mistakes.entrySet()){
                        if(e.getValue() == max){
                            worst.add(e.getKey());
                        }
                    }
                    if(worst.size()==1){
                        print("The hardest card is \"" + worst.get(0) + "\". You have " + mistakes.get(worst.get(0)) + " errors answering it.");
                    } else {
                        final StringBuilder builder = new StringBuilder("The hardest cards are");
                        worst.forEach(e -> {
                            builder.append(" \"").append(e).append("\"");
                            if(worst.get(worst.size()-1).equals(e)){
                                builder.append(".");
                            } else {
                                builder.append(",");
                            }
                        });
                        String multi = max == 1 ? "": "s";
                        builder.append(" You have ").append(max).append(" error").append(multi).append(" answering it.");
                        print(builder.toString());
                    }
                }
            } else if("reset stats".equals(command)){
                mistakes.replaceAll((s, v) -> 0);
                print("Card statistics has been reset.");
            } else if("exit".equals(command)){
                break;
            }
            print(askAboutCommand);
        }
        print("Bye bye!");
        if(exportOnEndFile != null){
            exportQuestions(new File(exportOnEndFile));
        }
    }
    private static void importQuestions(File file){
        int counter = 0;
        try(Scanner sc = new Scanner(file)) {
            String term;
            String definition;
            int mistake;
            while(sc.hasNext()){
                term = input(sc);
                if(sc.hasNext()){
                    definition = input(sc);
                    if(sc.hasNext()){
                        mistake = Integer.parseInt(input(sc));
                        questions.put(term, definition);
                        mistakes.put(term, mistake);
                        counter += 1;
                    }
                }
            }
            print(counter + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            print("File not found.");
        }
    }

    private static void exportQuestions(File file){
        int counter = 0;
        try(PrintWriter printWriter = new PrintWriter(file)){
            for(Map.Entry<String, String> e : questions.entrySet()){
                printWriter.println(e.getKey());
                printWriter.println(e.getValue());
                printWriter.println(mistakes.get(e.getKey()));
                counter += 1;
            }
            print(counter + " cards have been saved.");
        } catch (IOException e){
            print("File not found.");
        }
    }

    private static void saveLog(File file){
        try(PrintWriter printWriter = new PrintWriter(file)){
            printWriter.println(logger.toString());
            print("The log has been saved.");
        } catch (FileNotFoundException e) {
            print("File not found.");
        }
    }

    private static void print(String out){
        logger.append(out).append("\n");
        System.out.println(out);
    }

    private static String input(Scanner sc){
        String line = sc.nextLine();
        logger.append(line).append("\n");
        return line;
    }
}