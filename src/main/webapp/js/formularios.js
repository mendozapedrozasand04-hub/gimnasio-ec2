document.querySelectorAll('form').forEach(function(formulario) {
 formulario.addEventListener('submit', function(evento) {
  var mensaje = formulario.dataset.confirmar;
  if (mensaje && !confirm(mensaje)) { evento.preventDefault(); return; }
  // Esperar a que el navegador recoja el valor del boton antes de deshabilitarlo.
  if (formulario.method.toLowerCase() === 'post') {
   setTimeout(function() { formulario.querySelectorAll('button').forEach(function(boton) { boton.disabled = true; }); }, 0);
  }
 });
});
var membresia = document.getElementById('membresia-pago');
if (membresia) {
 function actualizarMonto() {
  document.getElementById('monto-pago').value = membresia.options[membresia.selectedIndex].dataset.monto || '';
 }
 membresia.addEventListener('change', actualizarMonto);
 actualizarMonto();
}
