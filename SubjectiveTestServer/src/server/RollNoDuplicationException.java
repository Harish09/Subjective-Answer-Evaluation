/*
 * Type of exception to be raised when more than one connection requests comes with same roll no.
 */
package server;

/**
 *
 * @author Parag Anand Guruji
 */
public class RollNoDuplicationException extends Exception{
    private static final long serialVersionUID = 1L;

    public RollNoDuplicationException(String message) {
        super(message);
    }
}
