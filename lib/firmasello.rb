#require 'openssl',:version=>'=0.9.4'
require 'bouncy-castle-java'
require 'base64'
require 'firmasello.jar'  

class CertificadoError < StandardError; end
class KeyPassError < CertificadoError; end
class CertificadoInvalidoError < CertificadoError; end
class SelloError < StandardError; end
class FirmaError < StandardError; end
class PropiedadesError < FirmaError; end
class PdfInvalidoError < FirmaError; end

class FirmaSello

	def sellar(certificado,contrasena,datos)
		java_import "mx.uaemex.fise.firmasello.Sello"
		#java_import "java.lang.String"
		java_import "mx.uaemex.fise.firmasello.exceptions.SelloException"
		java_import "mx.uaemex.fise.firmasello.exceptions.CertificadoException"
		java_import "mx.uaemex.fise.firmasello.exceptions.PrivateKeyException"
		java_import "mx.uaemex.fise.firmasello.exceptions.CertificadoInvalidoException"		
		begin
		sellado = Sello.new
		resultado = sellado.sellar(ordena_datos(datos),certificado.to_java_bytes,String.new(contrasena))
		rescue PrivateKeyException => e
   			raise KeyPassError.new"#{e.message}"
   		rescue CertificadoInvalidoException => e
   			raise CertificadoInvalidoError.new"#{e.message}"
   		rescue CertificadoException => e
   			raise CertificadoError.new"#{e.message}"
   		rescue SelloException => e
   			raise SelloError.new"#{e.message}"
   		end
		Hash["Cadena Original" => resultado.get(String.new("original")), "Cadena Sello" => resultado.get(String.new("sellado"))]
	end

	def firmar(certificado,contrasena,archivo,propiedades={})
		java_import "mx.uaemex.fise.firmasello.Firma"
		java_import "mx.uaemex.fise.firmasello.exceptions.FirmaException"
		java_import "mx.uaemex.fise.firmasello.exceptions.CertificadoException"
		java_import "mx.uaemex.fise.firmasello.exceptions.PrivateKeyException"
		java_import "mx.uaemex.fise.firmasello.exceptions.CertificadoInvalidoException"
		java_import "mx.uaemex.fise.firmasello.exceptions.PropiedadesException"
		java_import "mx.uaemex.fise.firmasello.exceptions.PdfInvalidoException"
		java_import "java.util.HashMap";
		props = HashMap.new
		unless propiedades[:motivo].nil?
			props.put(Firma::Propiedades.value_of("Motivo"),java.lang.String.new(propiedades[:motivo]))
		end
		unless propiedades[:ubicacion].nil?
			props.put(Firma::Propiedades.value_of("Ubicacion"),java.lang.String.new(propiedades[:ubicacion]))
		end
		unless propiedades[:nombre].nil?
			props.put(Firma::Propiedades.value_of("Nombre"),java.lang.String.new(propiedades[:nombre]))
		end
		unless propiedades[:x].nil?
			props.put(Firma::Propiedades.value_of("PosX"),java.lang.String.new(propiedades[:x].to_s))
		end
		unless propiedades[:y].nil?
			props.put(Firma::Propiedades.value_of("PosY"),java.lang.String.new(propiedades[:y].to_s))
		end
		unless propiedades[:tamx].nil?
			props.put(Firma::Propiedades.value_of("TamX"),java.lang.String.new(propiedades[:tamx].to_s))
		end
		unless propiedades[:tamy].nil?
			props.put(Firma::Propiedades.value_of("TamY"),java.lang.String.new(propiedades[:tamy].to_s))
		end
		unless propiedades[:pagina].nil?
			props.put(Firma::Propiedades.value_of("Pagina"),java.lang.String.new(propiedades[:pagina].to_s))
		end
		unless propiedades[:fondo].nil?
			props.put(Firma::Propiedades.value_of("Fondo"), propiedades[:fondo] == true ? java.lang.String.new("true") : java.lang.String.new(Base64.encode64(propiedades[:fondo])))
		end
		unless propiedades[:autografa].nil?
			props.put(Firma::Propiedades.value_of("Autografa"), java.lang.String.new(Base64.encode64(propiedades[:autografa])))
		end
    	unless propiedades[:mostrar_firma].nil?
			props.put(Firma::Propiedades.value_of("Mostrar_Firma"), java.lang.String.new(propiedades[:mostrar_firma].to_s))
		end
		begin
		firmado = Firma.new	
		firmado.firmar(archivo.to_java_bytes,certificado.to_java_bytes,java.lang.String.new(contrasena),props).to_s
		rescue PrivateKeyException => e
   			raise KeyPassError.new"#{e.message}"
   		rescue CertificadoInvalidoException => e
   			raise CertificadoInvalidoError.new"#{e.message}"
   		rescue CertificadoException => e
   			raise CertificadoError.new"#{e.message}"
   		rescue PdfInvalidoException => e
   			raise PdfInvalidoError.new"#{e.message}"
		rescue PropiedadesException => e
   			raise PropiedadesError.new"#{e.message}"
		rescue FirmaException => e
   			raise FirmaError.new"#{e.message}"
 		end
	end
	
	def ordena_datos(datos)
	 	datos_sello = "||1.0|"
		begin  
			datos.each do |x| 
				datos_sello.concat x 
				datos_sello.concat "|"
			end
		rescue
			datos_sello.concat datos
			datos_sello.concat "|"
		end
		datos_sello.concat "|"
	end 

end
