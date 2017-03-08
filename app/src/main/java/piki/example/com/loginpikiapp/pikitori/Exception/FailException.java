package piki.example.com.loginpikiapp.pikitori.Exception;

/**
 * Created by admin on 2017-02-16.
 */
public class FailException extends Exception{
    String message ;
    public FailException(String message){
        this.message = message;
    }
}
