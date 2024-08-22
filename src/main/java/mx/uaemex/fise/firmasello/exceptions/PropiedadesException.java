package mx.uaemex.fise.firmasello.exceptions;

/**
 * Errores en la definicion de las opciones de firmado
 * @author aiolivaresl
 */
public class PropiedadesException extends FirmaException {
	public static final long serialVersionUID = 1L;
	public PropiedadesException(String string) {
		super("Error de propiedades: "+string);
	}
	
}
