FirmaSello Core
=
Paquete java de firmado y sellado con gema de integracion a JRuby
-
Principales funciones:

-Sello: recibe una cadena de texto o arreglo en string y lo firma con el certificado dado

-Fimra: recibe un archivo pdf y agrega una firma digital con el certificado dado

___

Puede ser usado desde java agregando el jar (dependencias incluidas) o instalando la gema y ser usado desde JRuby (no ruby, por su dependencia java).

Compilacion
-
Prerequisitos: maven, jruby y rake

-Clona este repo

-Ejecuta rake

	rake gema
	
En raiz encontraras la gema firmasello.X.X.X.gem y dentro el folder **target** encontraras dos jar (con y sin dependencias) 

___

Uso
-
-Java:

	HashMap<Propiedades,String> propiedades = new HashMap<Propiedades,String>();
		
		propiedades.put(Propiedades.Autografa, Base64.encodeBase64String(firmaAutografaBytes));
		propiedades.put(Propiedades.Pagina, "1");
		propiedades.put(Propiedades.Nombre, "Firma 1");
		propiedades.put(Propiedades.PosX, "100");
		.
		.
		.

	pdfSalidaBytes = Firma.firmar(pdfEntradaBytes, certificadoBytes, contrasena));
	
	sello = Sello.sellar(Arrays.asList("hola", "mundo"),certificadoBytes,contrasena)
-Ruby:

	options = { :motivo=>"autorizacion",
					:ubicacion=>"dtic uaem",
					:x=>10,
					:y=>10,
					:tamy=>80,
					:autografa=>autografa
					.
					.
					.
				}
	
	pdf_firmado = FirmaSello.firmar(certificado,contrasena,pdf_entrada,options)
	
	sello = FirmaSello.sellar(certificado,contrasena,["hola","mundo"])
				
Para mas ejemplos ver test y src/test
	