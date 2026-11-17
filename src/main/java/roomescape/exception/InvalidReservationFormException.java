package roomescape.exception;

public class InvalidReservationFormException extends RuntimeException{
    public InvalidReservationFormException(String message){
        super(message);
    }
}
