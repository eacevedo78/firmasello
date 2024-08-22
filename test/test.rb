require 'minitest/spec'
require 'minitest/autorun'
require 'firmasello.rb'

class FirmaSelloTest < MiniTest::Test

	@@certificado = File.read('./src/test/resources/mx/uaemex/fise/firmasello/usuario_prueba.p12')
	@@certificado_vencido = File.read('./src/test/resources/mx/uaemex/fise/firmasello/cert_vencido.p12')
	@@pdf_vertical = File.binread('./src/test/resources/mx/uaemex/fise/firmasello/vertical.pdf')
	@@pdf_horizontal = File.binread('./src/test/resources/mx/uaemex/fise/firmasello/horizontal.pdf')
	@@img_autografa = File.binread('./src/test/resources/mx/uaemex/fise/firmasello/autografa.png')
	@@pass = "123456"
	@@texto = "hola"

  def test_sello
	servicio_firma = FirmaSello.new
	assert_instance_of Hash , servicio_firma.sellar(@@certificado,@@pass,@@texto)
  end

  def test_cert_error
 	servicio_firma = FirmaSello.new
  	assert_raises(KeyPassError) { servicio_firma.sellar(@@certificado,String.new,@@texto) }
  	assert_raises(KeyPassError) { servicio_firma.firmar(@@certificado,String.new,@@pdf_vertical) }
  end

  def test_cert_vencido
	servicio_firma = FirmaSello.new
  	assert_raises(CertificadoInvalidoError) { servicio_firma.sellar(@@certificado_vencido,@@pass,@@texto) }
  	assert_raises(CertificadoInvalidoError) { servicio_firma.firmar(@@certificado_vencido,@@pass,@@pdf_vertical) }
  end

  #def test_demasiadas_firmas
  #  for i in 0..10000
  #   test_varias_firmas
  #  end
  #end

  def test_varias_firmas
	servicio_firma = FirmaSello.new
  	data =  servicio_firma.firmar(@@certificado,@@pass,@@pdf_horizontal);
  	data =  servicio_firma.firmar(@@certificado,@@pass,data);
  	data =  servicio_firma.firmar(@@certificado,@@pass,data);
  	data =  servicio_firma.firmar(@@certificado,@@pass,data);
  	data =  servicio_firma.firmar(@@certificado,@@pass,data);
  	File.open("test-varias-firma-ruby.pdf", "wb") do |f|
   		f.write(data)
		end
		assert true
  end

  def test_firma_default
	servicio_firma = FirmaSello.new
  	data =  servicio_firma.firmar(@@certificado,@@pass,@@pdf_horizontal);
  	File.open("test-default1-ruby.pdf", "wb") do |f|
  		f.write(data)
		end
		assert true
  end

  def test_firma_default2
	servicio_firma = FirmaSello.new
   	propiedades = {
   		:motivo => "Lorem ipsum dolor sit amet"
   	}
  	data =  servicio_firma.firmar(@@certificado,@@pass,@@pdf_vertical,propiedades);
  	File.open("test-default2-ruby.pdf", "wb") do |f|
  		f.write(data)
		end
		assert true
  end

  def test_firma_prop1
	servicio_firma = FirmaSello.new
   	propiedades = {
   		:motivo => "Lorem ipsum dolor sit amet",
		:x => 130,
		:y => 50,
		:tamx => 100,
		:tamy => 100
   	}
  	data =  servicio_firma.firmar(@@certificado,@@pass,@@pdf_horizontal,propiedades);
  	File.open("test-props1-ruby.pdf", "wb") do |f|
  		f.write(data)
		end
		assert true
  end

  def test_firma_prop2
	servicio_firma = FirmaSello.new
   	propiedades = {
   		:motivo => "Lorem ipsum dolor sit amet",
   		:ubicacion => "Lorem ipsum dolor sit amet",
		:x => 130,
		:y => 50,
		:tamx => 100,
		:tamy => 100,
		:autografa => @@img_autografa
   	}
  	data =  servicio_firma.firmar(@@certificado,@@pass,@@pdf_vertical,propiedades);
  	File.open("test-props2-ruby.pdf", "wb") do |f|
  		f.write(data)
		end
		assert true
  end
  
end