package mx.uaemex.fise.firmasello;

import java.security.Signature;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import mx.uaemex.fise.firmasello.exceptions.CertificadoException;
import mx.uaemex.fise.firmasello.exceptions.SelloException;
import org.apache.commons.codec.binary.Base64;

/**
 * Metodos estaticos para el sellado
 * @author aiolivaresl
 */
public class Sello {
	
	private final String CADENA_ORIGINAL = "original";
    private final String CADENA_SELLO = "sellado";
    private final String VERSION = "1.0";
    
    //Constructor
    public Sello(){

    }
 
    /**
     * @param datos Cadena de texto a sellar, se toma como cadena original(no se modifica)
     * @param certificado Archivo p12 con que se firmara (en bytes)
     * @param contrasena Contrasena del archivo p12
     * @param alias Identificador del certificado a usar, null para usar el primero
     * @return Mapa del sello {original,sellado}
     * @throws CertificadoException Errores de certificado
     * @throws SelloException Errores en el proceso de sello
     */
    public Map<String,String> sellar(String datos, byte[] certificado,String contrasena,String alias) throws CertificadoException, SelloException{
    	return creaSello(datos,certificado,contrasena,alias);
    }
    
    /**
     *Igual que {@link #sellar(String, byte[], String, String) Sello.sellar(datos,certificado,contrasena,null)}
     */
    public Map<String,String> sellar(String datos, byte[] certificado,String contrasena) throws CertificadoException, SelloException{
    	return sellar(datos,certificado,contrasena,null);
    }
        
    /**
     * @param datos Coleccion de strings a sellar, se genera string con | como separador de los elementos
     * @param certificado Archivo p12 con que se firmara (en bytes)
     * @param contrasena Contrasena del archivo p12
     * @param alias Identificador del certificado a usar, null para usar el primero
     * @return Mapa del sello {original,sellado}
     * @throws CertificadoException Errores de certificado
     * @throws SelloException Errores en el proceso de sello
     */
    public Map<String,String> sellar(Collection<String> datos, byte[] certificado,String contrasena,String alias) throws CertificadoException, SelloException{
    	StringBuilder str = new StringBuilder("||").append(VERSION).append('|');
    	for(String dato : datos)
    		str.append(dato).append('|');
    	str.append('|');
    	return sellar(str.toString(),certificado,contrasena,alias);
    }
        
    /**
     *Igual que {@link #sellar(Collection, byte[], String, String) Sello.sellar(datos,certificado,contrasena,null)}
     */
    public Map<String,String> sellar(Collection<String> datos, byte[] certificado,String contrasena) throws CertificadoException, SelloException{
    	return sellar(datos,certificado,contrasena,null);
    }
         
    /**
     * Metodo interno de firmado
     * @see #sellar(String, byte[], String, String) sellar(cadenaOriginal,certificado,contrasena,alias)
     */
    private Map<String,String> creaSello(String cadenaOriginal, byte[] certificado,String contrasena,String alias) throws CertificadoException, SelloException {
    	Map<String,String> resultado = new HashMap<String,String>();
    	resultado.put(CADENA_ORIGINAL, cadenaOriginal);  
    	try {
    		Certificado cert = new Certificado(certificado,contrasena,alias);
            Signature dsa = Signature.getInstance("SHA1withRSA");
            dsa.initSign(cert.getPrivateKey());
            dsa.update(cadenaOriginal.getBytes());
            resultado.put(CADENA_SELLO, Base64.encodeBase64String(dsa.sign()));
			return resultado;
			}
    		catch (CertificadoException e) {
    			throw e;
    		} 
    		catch (Exception e) {
				throw new SelloException("Error irrecuperable de sistema: "+e.getMessage());
			} 
    }
   
}
