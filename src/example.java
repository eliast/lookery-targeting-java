import com.lookery.targeting.LookeryTargeting;

public class example {
    
    static final String apiKey = "<INSERT YOUR LOOKERY API KEY HERE>";
    static final String secretKey = "<INSERT YOUR LOOKERY SECRET KEY HERE>";
    
    
    public static void main(String args[]) throws Exception {
        if (apiKey.startsWith("<INSERT")) {
            System.err.println("Please examine example.java and update it with your credentials");
            System.exit(-1);
        }
        
        LookeryTargeting targeting = new LookeryTargeting(apiKey, secretKey);
        String redirect = targeting.redirect("http://www.example.com/?a={profile_yob}&g={profile_gender}");
        System.out.println(redirect); 
    }
}
