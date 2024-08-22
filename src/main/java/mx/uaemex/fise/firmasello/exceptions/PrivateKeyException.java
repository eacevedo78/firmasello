package mx.uaemex.fise.firmasello.exceptions;

/**
 * Contenedor de llaves publica/privada no puede ser leido, puede deberse a archivo p12 incorrecto o contrasena incorrecta 
 * @author aiolivaresl
 */
public class PrivateKeyException extends CertificadoException {
	public static final long serialVersionUID = 1L;
	public PrivateKeyException(String string) {
		super(string);
	}

}
