require 'rake/testtask'

Rake::TestTask.new do |t|
  t.libs << 'test'
end

task :default => [:gema]

desc "Generacion de gema firmasello"
task :gema do
  puts "Inicia Compilacion Java..."
  sh %{mvn install} do |ok, res|
  if ! ok
  	if res.exitstatus == 1
    raise "Error: No se completo la compilacion java (status = #{res.exitstatus})"
    else
    raise "Error: No se encontro maven instalado (status = #{res.exitstatus})"
    end
  end
  	puts "Compilacion Java completa"
  end
  puts "Preparando jar para gema..."
  cp('./target/firmasello.jar', './lib/firmasello.jar', :verbose => true)
  puts "Iniciando tests ruby..."
  Rake::Task['test'].invoke
  puts "Generando gema..."
  system "gem build firmasello.gemspec"
  puts "------------------------------------------------------------"
  puts "----------Se ha generado la gema con exito------------------"
  puts "------------------------------------------------------------"
end