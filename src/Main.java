
public class Main {
    public static void main(String [] args) {
        if(args.length != 1) {
            System.out.println("Incorrect Argument List");
            return;
        }
        System.out.println(TwitterAPIHandler.getWordCloud(args[0]));
    }
    
}   
