package mx.uaemex.fise.firmasello.exceptions;

/**
 * Errores al cargar llaves publica/privada
 * @author aiolivaresl
 * @see CertificadoInvalidoException
 * @see PrivateKeyException
 */
public class CertificadoException extends Exception {
	public static final long serialVersionUID = 1L;
	public CertificadoException(String string) {
		super(string);
	}
	
}
