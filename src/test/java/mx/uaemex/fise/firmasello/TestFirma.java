package mx.uaemex.fise.firmasello;

import com.itextpdf.text.DocumentException;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import mx.uaemex.fise.firmasello.Firma;
import mx.uaemex.fise.firmasello.Firma.Propiedades;
import mx.uaemex.fise.firmasello.exceptions.CertificadoException;
import mx.uaemex.fise.firmasello.exceptions.FirmaException;
import mx.uaemex.fise.firmasello.exceptions.PdfInvalidoException;
import mx.uaemex.fise.firmasello.exceptions.PropiedadesException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;

public class TestFirma {

    static String CONTRASENA = "123456";
    byte[] certificadoValido = loadFile("usuario_prueba.p12");
    byte[] pdfEntradaH = loadFile("horizontal.pdf");
    byte[] pdfEntradaV = loadFile("vertical.pdf");
    byte[] pdfEntradaV_varias_paginas = loadFile("vertical_varias_paginas.pdf");
    byte[] imgAutografa = loadFile("autografa.png");
    HashMap<Propiedades, String> propiedades = new HashMap<Propiedades, String>();

    public TestFirma() throws URISyntaxException, IOException {
    }

    private byte[] loadFile(String nombre) throws URISyntaxException, IOException {
        File archivo = new File(TestFirma.class.getResource(nombre).toURI());
        byte[] result = new byte[(int) archivo.length()];
        DataInputStream dataIs = new DataInputStream(new FileInputStream(archivo));
        dataIs.readFully(result);
        return result;
    }

    @Test(expected = PropiedadesException.class)
    public void errorPropiedades() throws CertificadoException, FirmaException {
        propiedades.put(Propiedades.PosX, "");
        Firma firmado = new Firma();
        firmado.firmar(pdfEntradaV, certificadoValido, CONTRASENA, propiedades);
    }

    @Test(expected = PdfInvalidoException.class)
    public void errorPdf() throws CertificadoException, FirmaException {
        Firma firmado = new Firma();
        firmado.firmar(new byte[0], certificadoValido, CONTRASENA);
    }

    @Test(expected = FirmaException.class)
    public void errorFirma() throws CertificadoException, FirmaException {
        propiedades.put(Propiedades.Pagina, "");
        Firma firmado = new Firma();
        firmado.firmar(pdfEntradaV, certificadoValido, CONTRASENA, propiedades);
    }

    @Ignore("Para ver uso de memoria")
    @Test
    public void testMuchisimasFirmas() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        for (int i = 0; i < 10000; i++) {
            Firma firmado = new Firma();
            firmado.firmar(firmado.firmar(firmado.firmar(firmado.firmar(firmado.firmar(pdfEntradaH, certificadoValido, CONTRASENA), certificadoValido, CONTRASENA), certificadoValido, CONTRASENA), certificadoValido, CONTRASENA), certificadoValido, CONTRASENA);
        }
    }

    @Test
    public void testVariasFirmas() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        FileOutputStream pdfSalida = new FileOutputStream("test-varias-firma.pdf");
        Firma firmado = new Firma();
        pdfSalida.write(firmado.firmar(firmado.firmar(firmado.firmar(firmado.firmar(firmado.firmar(pdfEntradaH, certificadoValido, CONTRASENA), certificadoValido, CONTRASENA), certificadoValido, CONTRASENA), certificadoValido, CONTRASENA), certificadoValido, CONTRASENA));
        pdfSalida.close();
    }

    @Test
    public void testFirmaDefault1() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        FileOutputStream pdfSalida = new FileOutputStream("test-default1.pdf");
        Firma firmado = new Firma();
        pdfSalida.write(firmado.firmar(pdfEntradaH, certificadoValido, CONTRASENA));
        pdfSalida.close();
    }

    @Test
    public void testFirmaDefault2() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        FileOutputStream pdfSalida = new FileOutputStream("test-default2.pdf");
        Firma firmado = new Firma();
        propiedades.put(Propiedades.Motivo, "Lorem ipsum dolor sit amet");
        pdfSalida.write(firmado.firmar(pdfEntradaV, certificadoValido, CONTRASENA, propiedades));
        pdfSalida.close();
    }

    @Test
    public void testFirmaProp1() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        FileOutputStream pdfSalida = new FileOutputStream("test-props1.pdf");
        Firma firmado = new Firma();
        propiedades.put(Propiedades.Motivo, "Lorem ipsum dolor sit amet");
        propiedades.put(Propiedades.Ubicacion, "Lorem ipsum dolor sit amet");
        propiedades.put(Propiedades.PosX, "130");
        propiedades.put(Propiedades.PosY, "50");
        propiedades.put(Propiedades.TamX, "100");
        propiedades.put(Propiedades.TamY, "100");
        pdfSalida.write(firmado.firmar(pdfEntradaH, certificadoValido, CONTRASENA, propiedades));
        pdfSalida.close();
    }

    @Test
    public void testFirmaProp2() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        FileOutputStream pdfSalida = new FileOutputStream("test-props2.pdf");
        Firma firmado = new Firma();
        propiedades.put(Propiedades.Motivo, "Lorem ipsum dolor sit amet");
        propiedades.put(Propiedades.Ubicacion, "Lorem ipsum dolor sit amet");
        propiedades.put(Propiedades.PosX, "130");
        propiedades.put(Propiedades.PosY, "50");
        propiedades.put(Propiedades.TamX, "100");
        propiedades.put(Propiedades.TamY, "100");
        propiedades.put(Propiedades.Autografa, Base64.encodeBase64String(imgAutografa));
        pdfSalida.write(firmado.firmar(pdfEntradaV, certificadoValido, CONTRASENA, propiedades));
        pdfSalida.close();
    }

    @Test
    public void testFirmaProp3() throws IOException, CertificadoException, FirmaException, URISyntaxException {
        FileOutputStream pdfSalida = new FileOutputStream("test-props3_ultima_pagina.pdf");
        Firma firmado = new Firma();
        propiedades.put(Propiedades.Pagina, "-1");
        pdfSalida.write(firmado.firmar(pdfEntradaV_varias_paginas, certificadoValido, CONTRASENA, propiedades));
        pdfSalida.close();
    }

    @Test
    public void testFirmaTodasLasPaginas() throws IOException, CertificadoException, FirmaException, URISyntaxException, DocumentException {
        FileOutputStream pdfSalida = new FileOutputStream("test-fima-todas-las-paginas.pdf");
        Firma firmado = new Firma();
        propiedades.put(Propiedades.Mostrar_Firma, "2");
        pdfSalida.write(firmado.firmar(pdfEntradaV_varias_paginas, certificadoValido, CONTRASENA, propiedades));
        pdfSalida.close();
    }

    @Test
    public void testFirmaTodasLasPaginasVariasFirmas() throws IOException, CertificadoException, FirmaException, URISyntaxException, DocumentException {
        FileOutputStream pdfSalida = new FileOutputStream("test-fima-todas-las-paginas-varias-firmas.pdf");
        Firma firmado = new Firma();
        HashMap<Propiedades, String> propiedades_firma_1 = new HashMap<Propiedades, String>();
        HashMap<Propiedades, String> propiedades_firma_2 = new HashMap<Propiedades, String>();
        HashMap<Propiedades, String> propiedades_firma_3 = new HashMap<Propiedades, String>();
        HashMap<Propiedades, String> propiedades_firma_4 = new HashMap<Propiedades, String>();
        HashMap<Propiedades, String> propiedades_firma_5 = new HashMap<Propiedades, String>();
        propiedades_firma_1.put(Propiedades.Mostrar_Firma, "2");
        propiedades_firma_2.put(Propiedades.Mostrar_Firma, "2");
        propiedades_firma_3.put(Propiedades.Mostrar_Firma, "2");
        propiedades_firma_4.put(Propiedades.Mostrar_Firma, "2");
        propiedades_firma_5.put(Propiedades.Mostrar_Firma, "2");
        pdfSalida.write(firmado.firmar(firmado.firmar(firmado.firmar(firmado.firmar(firmado.firmar(pdfEntradaV_varias_paginas, certificadoValido, CONTRASENA, propiedades_firma_5), certificadoValido, CONTRASENA, propiedades_firma_4), certificadoValido, CONTRASENA, propiedades_firma_3), certificadoValido, CONTRASENA, propiedades_firma_2), certificadoValido, CONTRASENA, propiedades_firma_1));
        pdfSalida.close();
    }

}
