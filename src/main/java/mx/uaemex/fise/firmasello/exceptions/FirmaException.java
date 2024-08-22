package mx.uaemex.fise.firmasello.exceptions;

/**
 * Errores al firmado
 * @author aiolivaresl
 * @see PropiedadesException
 * @see PdfInvalidoException
 */
public class FirmaException extends Exception {
	public static final long serialVersionUID = 1L;
	public FirmaException(String string) {
		super(string);
	}
	
}
