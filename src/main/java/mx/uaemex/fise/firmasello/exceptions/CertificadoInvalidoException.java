package mx.uaemex.fise.firmasello.exceptions;

/**
 * Error por la validez del certificado, puede ser por vencido, 
 * que aun no entra en vigor, revocado(temporal o definitivamente), etc.
 * @author aiolivaresl
 */
public class CertificadoInvalidoException extends CertificadoException {
	public static final long serialVersionUID = 1L;
	public CertificadoInvalidoException(String string) {
		super("Certificado no valido: "+string);
	}
	
}
