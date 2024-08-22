package mx.uaemex.fise.firmasello;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import mx.uaemex.fise.firmasello.exceptions.CertificadoException;
import mx.uaemex.fise.firmasello.exceptions.FirmaException;
import mx.uaemex.fise.firmasello.exceptions.PdfInvalidoException;
import mx.uaemex.fise.firmasello.exceptions.PropiedadesException;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Metodos estaticos para el firmado de pdf
 *
 * @author aiolivaresl
 */
public class Firma {

    private final ExternalDigest digest = new BouncyCastleDigest();
    private final int providerStatus = Security.addProvider(new BouncyCastleProvider());
    private final String fondoDefault = fondoEncode();

    private final String SIN_MOTIVO = "";
    private final String SIN_UBICACION = "";
    private final String DEFAULT_POS = "5";
    private final String DEFAULT_TAM_Y = "50";
    private final String DEFAULT_TAM_X = "90";
    private final String DEFAULT_PAG = "1"; //primera pagina del documento
    private final String DEFAULT_FONDO = "escudo.png";
    private final String DEFAULT_MOSTRAR_FIRMA = "1";
    private final String DEFAULT_NOMBRE = "firma";

    private ByteArrayOutputStream salida = new ByteArrayOutputStream();

    /**
     * <strong>Propiedades de la firma (opcionales):</strong>
     * <p>
     * <strong>Motivo</strong> - razon de la firma (en blanco por default)<br>
     * <strong>Ubicacion</strong> - localidad de firmado (en blanco por
     * default)<br>
     * <strong>Nombre</strong> - nombre del campo visual de la firma
     * (autogenerado por iText por default)<br>
     * <strong>PosX</strong> - posicion en x de la firma visible (0 por
     * default)<br>
     * <strong>PosY</strong> - posicion en y de la firma visible (0 por
     * default)<br>
     * <strong>Tamx</strong> - ancho de la firma visible (50 por default)<br>
     * <strong>TamY</strong> - alto de la firma visible (50 por default)<br>
     * <strong>Pagina</strong> - numero de pagina de la firma visible (1 por
     * default)<br>
     * <strong>Autografa</strong> - imagen encodeada Base64 para usarse como
     * firma autografa (ninguno por default) Nota: de darse una firma autografa
     * unicamente esta imagen sale en el campo visible quitando logo de fondo y
     * detalles de la firma<br>
     * <strong>Fondo</strong> - imagen de fondo de la firma visible (logo UAEM
     * por default)<br>
     * </p>
     *
     * @author aiolivaresl
     */
    public enum Propiedades {

        Motivo, Ubicacion, Nombre, PosX, PosY, TamX, TamY, Pagina, Autografa, Fondo, Mostrar_Firma
    }

    public Firma(){

    }
    
    /**
     * @param entrada Pdf a firmar en bytes
     * @param certificado Archivo p12 con que se firmara (en bytes)
     * @param contrasena Contrasena del archivo
     * @param alias Identificador del certificado a usar, null para usar el
     * primero
     * @param propiedades Mapa de {@link Propiedades opciones} del firmado
     * @return Pdf firmado (en bytes)
     * @throws CertificadoException Error de certificado
     * @throws FirmaException Error en el proceso de firma
     */
    public byte[] firmar(byte[] entrada, byte[] certificado, String contrasena, String alias, Map<Propiedades, String> propiedades) throws CertificadoException, FirmaException {
        return creaFirma(entrada, certificado, contrasena, alias, propiedades);
    }

    /**
     * Igual que
     * {@link #firmar(byte[], byte[], String, String, Map) Firma.firmar(entrada,certificado,contrasena,null,null)}
     */
    public byte[] firmar(byte[] entrada, byte[] certificado, String contrasena) throws CertificadoException, FirmaException {
        return firmar(entrada, certificado, contrasena, null, null);
    }

    /**
     * Igual que
     * {@link #firmar(byte[], byte[], String, String, Map) Firma.firmar(entrada,certificado,contrasena,null,propiedades)}
     */
    public byte[] firmar(byte[] entrada, byte[] certificado, String contrasena, Map<Propiedades, String> propiedades) throws CertificadoException, FirmaException {
        return firmar(entrada, certificado, contrasena, null, propiedades);
    }

    /**
     * Metodo interno de firmado
     *
     * @see
     * {@link #firmar(byte[], byte[], String, String, Map) Firma.firmar(entrada,certificado,alias,propiedades)}
     */
    private byte[] creaFirma(byte[] entrada, byte[] certificado, String contrasena, String alias, Map<Propiedades, String> propiedades) throws CertificadoException, FirmaException {

        salida.reset();

        propiedades = propiedades == null ? new HashMap<Propiedades, String>() : propiedades;
        propiedades.put(Propiedades.Fondo, propiedades.get(Propiedades.Fondo) != null ? propiedades.get(Propiedades.Fondo) : fondoDefault);
        propiedades.put(Propiedades.Motivo, propiedades.get(Propiedades.Motivo) != null ? propiedades.get(Propiedades.Motivo) : SIN_MOTIVO);
        propiedades.put(Propiedades.Ubicacion, propiedades.get(Propiedades.Ubicacion) != null ? propiedades.get(Propiedades.Ubicacion) : SIN_UBICACION);
        propiedades.put(Propiedades.PosX, propiedades.get(Propiedades.PosX) != null ? propiedades.get(Propiedades.PosX) : DEFAULT_POS);
        propiedades.put(Propiedades.PosY, propiedades.get(Propiedades.PosY) != null ? propiedades.get(Propiedades.PosY) : DEFAULT_POS);
        propiedades.put(Propiedades.TamX, propiedades.get(Propiedades.TamX) != null ? propiedades.get(Propiedades.TamX) : DEFAULT_TAM_X);
        propiedades.put(Propiedades.TamY, propiedades.get(Propiedades.TamY) != null ? propiedades.get(Propiedades.TamY) : DEFAULT_TAM_Y);
        propiedades.put(Propiedades.Pagina, propiedades.get(Propiedades.Pagina) != null ? propiedades.get(Propiedades.Pagina) : DEFAULT_PAG);
        propiedades.put(Propiedades.Mostrar_Firma, propiedades.get(Propiedades.Mostrar_Firma) != null ? propiedades.get(Propiedades.Mostrar_Firma) : DEFAULT_MOSTRAR_FIRMA);
        try {
            Certificado cert = new Certificado(certificado, contrasena, alias);
            PdfReader reader;
            float posicionX = Float.parseFloat(propiedades.get(Propiedades.PosX));
            float posicionY = Float.parseFloat(propiedades.get(Propiedades.PosY));
            float tamanoX = Float.parseFloat(propiedades.get(Propiedades.TamX));
            float tamanoY = Float.parseFloat(propiedades.get(Propiedades.TamY));
            if (Integer.parseInt(propiedades.get(Propiedades.Mostrar_Firma)) == 2) {
                propiedades.put(Propiedades.Nombre, propiedades.get(Propiedades.Nombre) != null ? propiedades.get(Propiedades.Nombre) : DEFAULT_NOMBRE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                propiedades.put(Propiedades.Nombre, new Long(calendar.getTimeInMillis()).toString());
                reader = new PdfReader(agregarSignatureFields(entrada, propiedades, posicionX, posicionY, tamanoX, tamanoY));          
            } else {
                reader = new PdfReader(entrada);
            }
            if (reader.getAcroFields().getTotalRevisions() > 0 && propiedades.get(Propiedades.PosX).equals(DEFAULT_POS)) {
                float total = (((Float.valueOf(DEFAULT_POS) * 2) + Float.valueOf(DEFAULT_TAM_X)) * reader.getAcroFields().getTotalRevisions());
                propiedades.put(Propiedades.PosX, String.valueOf(total));
                posicionX = Float.parseFloat(propiedades.get(Propiedades.PosX));
            }
            PdfStamper stamper = PdfStamper.createSignature(reader, salida, '\0', null, true);
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();        
            appearance.setReason(propiedades.get(Propiedades.Motivo));
            appearance.setLocation(propiedades.get(Propiedades.Ubicacion));
            if (propiedades.get(Propiedades.Fondo) != null) {
                appearance.setImage(Image.getInstance(Base64.decodeBase64(propiedades.get(Propiedades.Fondo))));
            }
            if (propiedades.get(Propiedades.Autografa) == null) {
                appearance.setRenderingMode(RenderingMode.DESCRIPTION);
            } else {
                appearance.setRenderingMode(RenderingMode.GRAPHIC);
                appearance.setSignatureGraphic(Image.getInstance(Base64.decodeBase64(propiedades.get(Propiedades.Autografa))));
                appearance.setImage(null);
            }
            //12-08-2015 se agrega funcionalidad para imprimir al firma en orden inverso
            int pagina;
            if (Integer.parseInt(propiedades.get(Propiedades.Pagina)) < 0) {
                pagina = reader.getNumberOfPages() + (Integer.parseInt(propiedades.get(Propiedades.Pagina)) + 1);
            } else {
                pagina = Integer.parseInt(propiedades.get(Propiedades.Pagina));
            }
            if (Integer.parseInt(propiedades.get(Propiedades.Mostrar_Firma)) == 2) {
                appearance.setVisibleSignature(propiedades.get(Propiedades.Nombre));
            } else {
                appearance.setVisibleSignature(new Rectangle(posicionX, posicionY, posicionX + tamanoX, posicionY + tamanoY), pagina, propiedades.get(Propiedades.Nombre));
            }
            BouncyCastleProvider provider = new BouncyCastleProvider();
            ExternalSignature externalSignature = new PrivateKeySignature(cert.getPrivateKey(), DigestAlgorithms.SHA256, provider.getName());
            MakeSignature.signDetached(appearance, digest, externalSignature, cert.getCertificateChain(), null, null, null, 0, CryptoStandard.CMS);
            return salida.toByteArray();
        } catch (InvalidPdfException e) {         
            throw new PdfInvalidoException("Verificar archivo de entrada");
        } catch (CertificadoException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new PropiedadesException("Campo numerico incorrecto");
        } catch (IOException e) {
            throw new PropiedadesException("Campo imagene incorrecto");
        } catch (IllegalArgumentException e) {          
            throw new PropiedadesException("Campos nombre y/o pagina incorrecto");
        } catch (Exception e) {
           e.printStackTrace();
            throw new FirmaException("Error irrecuperable de sistema: " + e.getMessage());
        }
    }

    public void createPdf(String filename) throws IOException, DocumentException {
        // step 1: Create a Document
        Document document = new Document();
        // step 2: Create a PdfWriter
        PdfWriter writer = PdfWriter.getInstance(
                document, new FileOutputStream(filename));
        // step 3: Open the Document
        document.open();
        // step 4: Add content
        document.add(new Paragraph("Hello World!"));
        // create a signature form field
        PdfFormField field = PdfFormField.createSignature(writer);
        field.setFieldName("prueba");
        // set the widget properties
        field.setPage();
        field.setWidget(
                new Rectangle(72, 732, 144, 780), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setFlags(PdfAnnotation.FLAGS_PRINT);
        // add it as an annotation
        writer.addAnnotation(field);
        // maybe you want to define an appearance
        PdfAppearance tp = PdfAppearance.createAppearance(writer, 72, 48);
        tp.setColorStroke(BaseColor.BLUE);
        tp.setColorFill(BaseColor.LIGHT_GRAY);
        tp.rectangle(0.5f, 0.5f, 71.5f, 47.5f);
        tp.fillStroke();
        tp.setColorFill(BaseColor.BLUE);
        ColumnText.showTextAligned(tp, Element.ALIGN_CENTER,
                new Phrase("SIGN HERE"), 36, 24, 25);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        // step 5: Close the Document
        document.close();
    }

    public byte[] agregarSignatureFields(byte[] entrada, Map<Propiedades, String> propiedades, float posicionX, float posicionY, float tamanoX, float tamanoY) throws IOException, DocumentException {
        ByteArrayOutputStream salida_campos_firma = new ByteArrayOutputStream();
        salida_campos_firma.reset();
        PdfReader reader = new PdfReader(entrada);
        PdfStamper stamper = new PdfStamper(reader, salida_campos_firma, '\0', true);
        PdfFormField field = PdfFormField.createSignature(stamper.getWriter());
        field.setFieldName(propiedades.get(Propiedades.Nombre));
        if (reader.getAcroFields().getTotalRevisions() > 0 && propiedades.get(Propiedades.PosX).equals(DEFAULT_POS)) {
            float total = (((Float.valueOf(DEFAULT_POS) * 2) + Float.valueOf(DEFAULT_TAM_X)) * reader.getAcroFields().getTotalRevisions());
            propiedades.put(Propiedades.PosX, String.valueOf(total));
            posicionX = Float.parseFloat(propiedades.get(Propiedades.PosX));          
        }       
        field.setWidget(new Rectangle(posicionX, posicionY, posicionX + tamanoX, posicionY + tamanoY), PdfAnnotation.HIGHLIGHT_OUTLINE);
        field.setFlags(PdfAnnotation.FLAGS_PRINT);
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            stamper.addAnnotation(field, i);
        }
        stamper.close();
        return salida_campos_firma.toByteArray();
    }

    private String fondoEncode() {
        byte[] bytes = new byte[15360];
        DataInputStream dataIs = new DataInputStream(Firma.class.getResourceAsStream(DEFAULT_FONDO));
        try {
            dataIs.readFully(bytes);
        } catch (IOException e) {
        }
        return Base64.encodeBase64String(bytes);
    }
}
