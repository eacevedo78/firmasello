package mx.uaemex.fise.firmasello;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import mx.uaemex.fise.firmasello.exceptions.CertificadoException;
import mx.uaemex.fise.firmasello.exceptions.CertificadoInvalidoException;
import mx.uaemex.fise.firmasello.exceptions.PrivateKeyException;
import org.junit.Test;

public class TestCertificado {

	byte[] certificadoValido= loadCert("usuario_prueba.p12");
	byte[] certificadoVencido= loadCert("cert_vencido.p12");
	byte[] certificadoRevocado= loadCert("cert_revocado.p12");
	static String CONTRASENA = "123456";
	
	public TestCertificado() throws URISyntaxException, IOException{}
	
	private byte[] loadCert(String nombre) throws URISyntaxException, IOException{
		File archivo = new File(TestCertificado.class.getResource(nombre).toURI()); 
		byte[] result = new byte[(int)archivo.length()];
		DataInputStream dataIs = new DataInputStream(new FileInputStream(archivo));
		dataIs.readFully(result);
		return result;
	}
	
	@Test(expected=PrivateKeyException.class)
	public void archivoCorrupto() throws CertificadoException{
		new Certificado(new byte[0],CONTRASENA,null);
	}
	
	@Test(expected=PrivateKeyException.class)
	public void contrasenaIncorrecta() throws CertificadoException{
		new Certificado(certificadoValido,"",null);
	}
	
	@Test
	public void valido() throws CertificadoException{
		assertTrue(new Certificado(certificadoValido,CONTRASENA,null) instanceof Certificado);
	}
	
	@Test(expected=CertificadoInvalidoException.class)
	public void vencido() throws CertificadoException{
		new Certificado(certificadoVencido,CONTRASENA,null);
	}
		
	@Test
	public void revocado(){
		try {
			new Certificado(certificadoRevocado,CONTRASENA,null);
			System.out.println("No se pudo verificar OCSP");
		} catch (CertificadoException e) {
			System.out.println("Verificado OCSP satisfactoriamente");
			assertTrue(e instanceof CertificadoInvalidoException);
		} 
	}
	
		
}
