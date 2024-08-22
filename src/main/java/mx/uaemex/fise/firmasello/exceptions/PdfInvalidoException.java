package mx.uaemex.fise.firmasello.exceptions;

/**
 * Archivo pdf invalido o corrupto 
 * @author aiolivaresl
 */
public class PdfInvalidoException extends FirmaException {
	public static final long serialVersionUID = 1L;
	public PdfInvalidoException(String string) {
		super("Pdf invalido: "+string);
	}
	
}
