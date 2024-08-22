package mx.uaemex.fise.firmasello;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import sun.security.provider.certpath.OCSP;
import sun.security.provider.certpath.OCSP.RevocationStatus;
import sun.security.provider.certpath.OCSP.RevocationStatus.CertStatus;
import mx.uaemex.fise.firmasello.exceptions.CertificadoException;
import mx.uaemex.fise.firmasello.exceptions.CertificadoInvalidoException;
import mx.uaemex.fise.firmasello.exceptions.PrivateKeyException;

/**
 * Extrae y valida certificado desde un archivo p12
 *
 * @author aiolivaresl
 */
public class Certificado {

    private PrivateKey privateKey;
    private Certificate[] chain;
    private X509Certificate certificate;

    /**
     * @param certificado Archivo p12 en bytes
     * @param contrasena Contrasena del archivo p12
     * @param alias Identificador del certificado a usar, null para usar el
     * primero
     * @throws CertificadoException Errores al leer el certificado
     * <p>
     * - CertificadoInvalidoException si al momento el certificado no tiene
     * validez<br/>
     * - PrivateKeyException si el archivo p12 no es un archivo correcto o la
     * contrasenia es incorrecta<br/>
     * -CertificadoException cualquier otro error que impida usar el certificado
     * </p>
     */
    public Certificado(byte[] certificado, String contrasena, String alias)
            throws CertificadoException {

        try {
            KeyStore keyStoreFile = KeyStore.getInstance("PKCS12");
            keyStoreFile.load(new ByteArrayInputStream(certificado),
                    contrasena.toCharArray());
            alias = alias == null ? keyStoreFile.aliases().nextElement()
                    : alias;
            certificate = (X509Certificate) keyStoreFile.getCertificate(alias);
            chain = keyStoreFile.getCertificateChain(alias);
            certificate.checkValidity();
            //checkAgainstOCSP();
            privateKey = (PrivateKey) keyStoreFile.getKey(alias,
                    contrasena.toCharArray());
        } catch (CertificateExpiredException e) {
            throw new CertificadoInvalidoException("Ha expirado");
        } catch (CertificateNotYetValidException e) {
            throw new CertificadoInvalidoException("No ha entrado en vigor");
        } catch (CertificateRevokedException e) {
            throw new CertificadoInvalidoException("Certificado revocado por "
                    + e.getAuthorityName().getName() + " desde "
                    + e.getRevocationDate() + " por causa "
                    + e.getRevocationReason());
        } catch (IOException e) {
            throw new PrivateKeyException(
                    "PrivateKey y/o contrasena incorrectos");
        } catch (Exception e) {
            throw new CertificadoException("Error irrecuperable de sistema: "
                    + e.getMessage());
        }
    }

    /*
    private void checkAgainstOCSP() throws CertificateRevokedException {
        RevocationStatus status;
        try {
            status = OCSP.check(certificate,
                    ((X509Certificate) chain[chain.length - 1]));
            if (status.getCertStatus() == CertStatus.REVOKED) {
                throw new CertificateRevokedException(
                        status.getRevocationTime(),
                        status.getRevocationReason(),
                        certificate.getIssuerX500Principal(),
                        status.getSingleExtensions());
            }
        } catch (CertPathValidatorException e) {
			// TODO logs para saber que paso - tiene que ver con los
            // certificados
        } catch (IOException e) {
            // TODO logs para saber que paso - tiene que ver con la red
        }
    }
    */

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public Certificate[] getCertificateChain() {
        return chain;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

}
